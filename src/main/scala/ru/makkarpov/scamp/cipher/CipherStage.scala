package ru.makkarpov.scamp.cipher

import akka.stream.{Attributes, BidiShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString
import ru.makkarpov.scamp.cipher.StreamCipher.IdentityCipher

object CipherStage extends GraphStage[BidiShape[ByteString, ByteString, CipherMessage, ByteString]] {
  val netIn = Inlet[ByteString]("network-in")
  val plainOut = Outlet[ByteString]("plain-out")
  val plainIn = Inlet[CipherMessage]("plain-in")
  val netOut = Outlet[ByteString]("network-out")

  override def shape: BidiShape[ByteString, ByteString, CipherMessage, ByteString] =
    BidiShape.of(netIn, plainOut, plainIn, netOut)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      var incomingCipher: StreamCipher = IdentityCipher
      var outgoingCipher: StreamCipher = IdentityCipher

      setHandler(netIn, new InHandler {
        override def onPush(): Unit = emit(plainOut, incomingCipher(grab(netIn)))
      })

      setHandler(plainOut, new OutHandler {
        override def onPull(): Unit = pull(netIn)
      })

      setHandler(plainIn, new InHandler {
        override def onPush(): Unit = grab(plainIn) match {
          case CipherMessage.Data(data) => emit(netOut, outgoingCipher(data))
          case CipherMessage.Enable(key, factory) =>
            incomingCipher = factory(key, forEncryption = false)
            outgoingCipher = factory(key, forEncryption = true)
            pull(plainIn)
        }
      })

      setHandler(netOut, new OutHandler {
        override def onPull(): Unit = pull(plainIn)
      })
    }
}
