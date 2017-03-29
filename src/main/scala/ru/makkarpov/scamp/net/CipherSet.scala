package ru.makkarpov.scamp.net

import akka.util.ByteString
import ru.makkarpov.scamp.StreamCipher

/**
  * Creation date: 22.10.2015
  * Copyright (c) harati
  */
object CipherSet {
	val empty = new CipherSet {
		override def decrypt(s: ByteString): ByteString = s
		override def encrypt(s: ByteString): ByteString = s
	}

	def apply(upstream: StreamCipher.Cipher, downstream: StreamCipher.Cipher): CipherSet = new CipherSet {
		override def decrypt(s: ByteString): ByteString = upstream(s)
		override def encrypt(s: ByteString): ByteString = downstream(s)
	}

	def apply(key: ByteString, cipher: StreamCipher.CipherFactory): CipherSet =
		CipherSet(cipher(key.toArray, forEncryption = false), cipher(key.toArray, forEncryption = true))
}

trait CipherSet {
	def decrypt(s: ByteString): ByteString
	def encrypt(s: ByteString): ByteString
}