package ru.makkarpov.scamp.net

import akka.stream._
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.{ByteString, ByteStringBuilder}
import ru.makkarpov.scamp.VarIntUtils
import ru.makkarpov.scamp.cipher.CipherMessage

// TODO: Add support for packet length limit (since the whole packet needs to be buffered in memory)
class FramingStage extends GraphStage[BidiShape[ByteString, ByteString, CipherMessage, CipherMessage]] {
  val netIn = Inlet[ByteString]("network-in")
  val pckOut = Outlet[ByteString]("packet-out")
  val pckIn = Inlet[CipherMessage]("packet-in")
  val netOut = Outlet[CipherMessage]("network-out")

  override def shape: BidiShape[ByteString, ByteString, CipherMessage, CipherMessage] =
    BidiShape.of(netIn, pckOut, pckIn, netOut)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      var framer = Framer.empty

      setHandler(netIn, new InHandler {
        override def onPush(): Unit = {
          val (newFramer, data) = framer(grab(netIn))
          framer = newFramer
          emitMultiple(pckOut, data)
        }
      })

      setHandler(pckOut, new OutHandler {
        override def onPull(): Unit = pull(netIn)
      })

      setHandler(pckIn, new InHandler {
        override def onPush(): Unit = grab(pckIn) match {
          case CipherMessage.Data(data) =>
            val dst = new ByteStringBuilder
            VarIntUtils.writeVarInt(dst, data.length)
            emit(netOut, CipherMessage.Data(dst.result() ++ data))

          case x => emit(netOut, x)
        }
      })

      setHandler(netOut, new OutHandler {
        override def onPull(): Unit = pull(pckIn)
      })
    }
}
