package ru.makkarpov.scamp

import akka.util.ByteString
import ru.makkarpov.scamp.cipher.StreamCipher

object Packet {
  sealed abstract class Meta extends Packet

  case class EnableEncryption(key: ByteString, factory: StreamCipher.Factory) extends Meta
}

abstract class Packet
