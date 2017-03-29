package ru.makkarpov.scamp.misc

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Tcp}
import akka.stream.stage.{GraphStageLogic, InHandler, OutHandler, GraphStageWithMaterializedValue => GS}
import akka.stream._
import akka.stream.javadsl.BidiFlow
import akka.stream.scaladsl.Tcp.OutgoingConnection
import akka.util.ByteString
import ru.makkarpov.scamp.handshake.PacketHandshake
import ru.makkarpov.scamp.status.client.{StatusPong, StatusResponse}
import ru.makkarpov.scamp.status.server.{StatusPing, StatusRequest}
import ru.makkarpov.scamp.{Packet, Scamp}

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}

object ServerPinger {
  def ping(addr: InetSocketAddress, timeout: Duration = Duration.Inf, readTimeout: Duration = Duration.Inf)
          (implicit sys: ActorSystem, mat: ActorMaterializer): Future[ServerPingResult] =
  {
    val base = Scamp.connect(addr, timeout)
    val timed =
      if (readTimeout.isFinite()) base.join(BidiFlow.bidirectionalIdleTimeout[Packet, Packet](readTimeout.asInstanceOf[FiniteDuration]))
      else base

    timed.joinMat(new ServerPinger(addr))(Keep.right).run()
  }
}

class ServerPinger(addr: InetSocketAddress) extends GS[FlowShape[Packet, Packet], Future[ServerPingResult]] {
  val in = Inlet[Packet]("in")
  val out = Outlet[Packet]("out")

  override def shape: FlowShape[Packet, Packet] = FlowShape.of(in, out)
  override def createLogicAndMaterializedValue(inheritedAttributes: Attributes): (GraphStageLogic, Future[ServerPingResult]) = {
    val promise = Promise[ServerPingResult]
    val logic = new GraphStageLogic(shape) {
      var jsonString = ""
      var pingSent = 0L

      var pulled = false
      var toSend = Seq.empty[Packet]

      def send(p: Packet): Unit =
        if (pulled) {
          emit(out, p)
          pulled = false
        } else toSend :+= p

      override def preStart(): Unit = {
        send(PacketHandshake(47, addr.getHostName, addr.getPort, PacketHandshake.NextState.Status))
        send(StatusRequest())

        pull(in)
      }

      setHandler(in, new InHandler {
        override def onPush(): Unit = grab(in) match {
          case StatusResponse(json) =>
            jsonString = json
            pingSent = System.currentTimeMillis()
            send(StatusPing(pingSent))
            pull(in)

          case StatusPong(_) =>
            val pingTime = System.currentTimeMillis() - pingSent
            promise.trySuccess(ServerPingResult.Success(jsonString, pingTime.toInt))
            completeStage()
        }

        override def onUpstreamFailure(ex: Throwable): Unit = {
          println("Pinger: " + ex)
          completeStage()
          promise.trySuccess(ServerPingResult.Failure)
        }

        override def onUpstreamFinish(): Unit = {
          completeStage()
          promise.trySuccess(ServerPingResult.Failure)
        }
      })

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          if (toSend.isEmpty) pulled = true
          else {
            emitMultiple(out, toSend.iterator)
            toSend = Nil
          }
        }

        override def onDownstreamFinish(): Unit = {
          completeStage()
          promise.trySuccess(ServerPingResult.Failure)
        }
      })
    }

    logic -> promise.future
  }
}
