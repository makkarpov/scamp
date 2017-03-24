package ru.makkarpov.scamp

import akka.util.ByteString

/**
  * Creation date: 22.10.2015
  * Copyright (c) harati
  */

object CipherSet {

	val empty = new CipherSet {
		override def decrypt(s: ByteString): ByteString = s
		override def encrypt(s: ByteString): ByteString = s
	}

	def apply(upstream: StreamCipher.Cipher, downstream: StreamCipher.Cipher) = new CipherSet {
		override def decrypt(s: ByteString): ByteString = upstream(s)
		override def encrypt(s: ByteString): ByteString = downstream(s)
	}

}

trait CipherSet {
	def decrypt(s: ByteString): ByteString
	def encrypt(s: ByteString): ByteString
}