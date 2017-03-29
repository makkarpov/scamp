package ru.makkarpov.scamp.cipher

import akka.util.ByteString

object CipherMessage {
  case class Data(data: ByteString) extends CipherMessage
  case class Enable(key: ByteString, factory: StreamCipher.Factory, sendBefore: Option[ByteString]) extends CipherMessage
}

sealed trait CipherMessage
