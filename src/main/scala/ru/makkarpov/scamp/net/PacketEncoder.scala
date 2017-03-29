package ru.makkarpov.scamp.net

import akka.stream.{Attributes, BidiShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString
import ru.makkarpov.scamp.handshake.PacketHandshake
import ru.makkarpov.scamp.handshake.PacketHandshake.NextState
import ru.makkarpov.scamp.{Packet, ProtocolState, StreamCipher}
import ru.makkarpov.scamp.tags.{PacketTag, TaggedPacket}

class PacketEncoder(isServer: Boolean) extends GraphStage[BidiShape[ByteString, Packet, Packet, ByteString]] {
  val in1  = Inlet[ByteString]("in1")
  val out1 = Outlet[Packet]("out1")
  val in2  = Inlet[Packet]("in2")
  val out2 = Outlet[ByteString]("out2")

  override def shape: BidiShape[ByteString, Packet, Packet, ByteString] =
    BidiShape.of(in1, out1, in2, out2)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      var cipher = CipherSet.empty
      var compressor = Compressor.empty
      var state = ProtocolState.Handshaking.toState(isServer)

      setHandler(in1, new InHandler {
        override def onPush(): Unit = {
          emit(out1, state.read(compressor.decompress(cipher.decrypt(grab(in1)))))
        }

        override def onUpstreamFailure(ex: Throwable): Unit = {
          println(s"Codec.Decoder: $ex")
          super.onUpstreamFailure(ex)
        }
      })

      setHandler(out1, new OutHandler {
        override def onPull(): Unit = pull(in1)
      })

      setHandler(in2, new InHandler {
        override def onPush(): Unit = {
          val packet = grab(in2)

          val tags = packet match {
            case TaggedPacket(_, x) => x
            case _ => Nil
          }

          val packetData = packet match {
            case TaggedPacket(x, _) => state.write(x)
            case _ => state.write(packet)
          }

          emit(out2, cipher.encrypt(compressor.compress(packetData)))

          packet match {
            case PacketHandshake(_, _, _, NextState.Login) =>
              throw new RuntimeException("No login state yet")

            case PacketHandshake(_, _, _, NextState.Status) =>
              state = ProtocolState.Status.toState(isServer)

            case _ =>
          }

          for (t <- tags) t match {
            case PacketTag.EnableCompression(threshold, level) =>
              compressor = Compressor(threshold, level)

            case PacketTag.EnableEncryption(key) =>
              cipher = CipherSet(key, StreamCipher.AESCFB8)
          }
        }

        override def onUpstreamFailure(ex: Throwable): Unit = {
          println(s"Codec.Encoder: $ex")
          super.onUpstreamFailure(ex)
        }
      })

      setHandler(out2, new OutHandler {
        override def onPull(): Unit = pull(in2)
      })
    }
}
