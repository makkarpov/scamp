package ru.makkarpov.scamp.cipher

import akka.util.ByteString

object AESCFB8 extends StreamCipher.Factory {
  override def apply(key: ByteString, forEncryption: Boolean): StreamCipher =
    new AESCFB8(key.toArray, forEncryption)
}

class AESCFB8(k: Array[Byte], forEncryption: Boolean) extends StreamCipher {
  if (k.length != 16)
    throw new IllegalArgumentException("Only 128 bit keys are supported")

  private val engine = new AESFastEngine
  private val state = new Array[Byte](16)
  private val tstate = new Array[Byte](16)

  engine.init(true, k)
  System.arraycopy(k, 0, state, 0, 16)

  override def apply(b: Byte): Byte = {
    engine.processBlock(state, 0, tstate, 0)
    val ret = (b ^ tstate(0)).toByte
    System.arraycopy(state, 1, state, 0, 15)
    state(15) = if (forEncryption) ret else b
    ret
  }
}