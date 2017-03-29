package ru.makkarpov.scamp.net

import akka.NotUsed
import akka.stream.javadsl.BidiFlow
import akka.stream.scaladsl.Flow
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream._
import akka.util.{ByteString, ByteStringBuilder}
import ru.makkarpov.scamp.VarIntUtils

class PacketFramer extends GraphStage[BidiShape[ByteString, ByteString, ByteString, ByteString]] {
  val in1 = Inlet[ByteString]("in1")
  val out1 = Outlet[ByteString]("out1")
  val in2 = Inlet[ByteString]("in2")
  val out2 = Outlet[ByteString]("out2")

  override def shape: BidiShape[ByteString, ByteString, ByteString, ByteString] =
    BidiShape.of(in1, out1, in2, out2)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      var codec = Codec.empty

      setHandler(in1, new InHandler {
        override def onPush(): Unit = {
          val (newCodec, data) = codec(grab(in1))
          codec = newCodec
          emitMultiple(out1, data)
        }

        override def onUpstreamFailure(ex: Throwable): Unit = {
          println(s"Framer.Decoder: $ex")
          super.onUpstreamFailure(ex)
        }
      })

      setHandler(out1, new OutHandler {
        override def onPull(): Unit = pull(in1)
      })

      setHandler(in2, new InHandler {
        override def onPush(): Unit = {
          val data = grab(in2)
          val dst = new ByteStringBuilder
          VarIntUtils.writeVarInt(dst, data.length)
          emit(out2, dst.result() ++ data)
        }

        override def onUpstreamFailure(ex: Throwable): Unit = {
          println(s"Framer.Encoder: $ex")
          super.onUpstreamFailure(ex)
        }
      })

      setHandler(out2, new OutHandler {
        override def onPull(): Unit = pull(in2)
      })
    }
}
