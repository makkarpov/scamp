package ru.makkarpov.scamp.cipher

import akka.util.ByteString

object StreamCipher {
  trait Factory {
    def apply(key: ByteString, forEncryption: Boolean): StreamCipher
  }

  object IdentityCipher extends StreamCipher {
    override def apply(b: Byte): Byte = b
    override def apply(bs: ByteString): ByteString = bs
  }
}

trait StreamCipher {
  def apply(b: Byte): Byte
  def apply(bs: ByteString): ByteString = bs.map(apply)
}
