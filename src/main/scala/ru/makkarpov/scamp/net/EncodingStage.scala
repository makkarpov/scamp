package ru.makkarpov.scamp.net

import akka.stream.{Attributes, BidiShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString
import ru.makkarpov.scamp.cipher.CipherMessage
import ru.makkarpov.scamp.protocol.handshake.PacketHandshake
import ru.makkarpov.scamp.protocol.handshake.PacketHandshake.NextState
import ru.makkarpov.scamp.{Packet, ProtocolState}

object EncodingStage {
  val Server = new EncodingStage(isServer = true)
  val Client = new EncodingStage(isServer = false)
}

class EncodingStage(isServer: Boolean) extends GraphStage[BidiShape[ByteString, Packet, Packet, CipherMessage]] {
  val netIn  = Inlet[ByteString]("network-in")
  val pckOut = Outlet[Packet]("packet-out")
  val pckIn  = Inlet[Packet]("packet-in")
  val netOut = Outlet[CipherMessage]("network-out")

  override def shape: BidiShape[ByteString, Packet, Packet, CipherMessage] =
    BidiShape.of(netIn, pckOut, pckIn, netOut)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      var compressor = Compressor.identity
      var state = ProtocolState.Handshaking.toState(isServer)

      def applyRecvTransitions(pck: Packet): Unit = pck match {
        case PacketHandshake(_, _, _, nextState) if isServer =>
          state = NextState.toProtocol(nextState).toState(isServer)

        // TODO: Login success on client
        // TODO: Set compression on client
        case _ =>
      }

      def applySentTransitions(pck: Packet): Unit = pck match {
        case PacketHandshake(_, _, _, nextState) if !isServer =>
          state = NextState.toProtocol(nextState).toState(isServer)

        // TODO: Login success on server
        // TODO: Set compression on server
        case _ =>
      }

      setHandler(netIn, new InHandler {
        override def onPush(): Unit = {
          val recv = state.read(compressor.decompress(grab(netIn)))
          applyRecvTransitions(recv)
          emit(pckOut, recv)
        }
      })

      setHandler(pckOut, new OutHandler {
        override def onPull(): Unit = pull(netIn)
      })

      setHandler(pckIn, new InHandler {
        override def onPush(): Unit = grab(pckIn) match {
          case meta: Packet.Meta => meta match {
            case Packet.EnableEncryption(key, factory) =>
              emit(netOut, CipherMessage.Enable(key, factory))
          }

          case other =>
            emit(netOut, CipherMessage.Data(compressor.compress(state.write(other))))
            applySentTransitions(other)
        }
      })

      setHandler(netOut, new OutHandler {
        override def onPull(): Unit = pull(pckIn)
      })
    }
}
