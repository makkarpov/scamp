package ru.makkarpov.scamp

import akka.util.ByteString

/**
 * Copyright © makkarpov, 2015
 * Creation date: 13.09.15
 */
object StreamCipher {
  sealed abstract class Cipher {
    def apply(b: Byte): Byte
    def apply(b: ByteString): ByteString = b.map(apply)
  }

  sealed abstract class CipherFactory {
    def apply(k: Array[Byte], forEncryption: Boolean): Cipher
  }

  object AESCFB8 extends CipherFactory
  case class AESCFB8(k: Array[Byte], forEncryption: Boolean) extends Cipher {
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
}