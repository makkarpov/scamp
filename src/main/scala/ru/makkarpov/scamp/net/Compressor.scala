package ru.makkarpov.scamp.net

import java.io.ByteArrayOutputStream
import java.util.zip.{DeflaterOutputStream, InflaterOutputStream}

import akka.util.{ByteString, ByteStringBuilder}
import ru.makkarpov.scamp.VarIntUtils

object Compressor {
  val empty = new Compressor {
    override def decompress(bs: ByteString): ByteString = bs
    override def compress(bs: ByteString): ByteString = bs
  }

  def apply(threshold: Int, level: Int): Compressor = new Compressor {
    override def decompress(bs: ByteString) = {
      val iter = bs.iterator
      val dataLen = VarIntUtils.readVarInt(iter)

      // -----BEGIN OF SHIT-----

      val rawBld = new ByteStringBuilder
      val inflaterOutputStream = new InflaterOutputStream(rawBld.asOutputStream)
      inflaterOutputStream.write(iter.toArray)
      inflaterOutputStream.close()
      val raw = rawBld.result()

      // -----END OF SHIT-----

      if (raw.length != dataLen)
        throw new IllegalArgumentException(s"Unexpected decompression result: length = ${raw.length}, dataLen field = $dataLen")

      raw
    }

    override def compress(bs: ByteString) = {
      val dst = new ByteStringBuilder
      VarIntUtils.writeVarInt(dst, bs.length)

      // -----BEGIN OF SHIT-----

      val deflaterOutputStream = new DeflaterOutputStream(dst.asOutputStream)
      deflaterOutputStream.write(bs.toArray)
      deflaterOutputStream.close()

      // -----END OF SHIT-----

      dst.result()
    }
  }
}

trait Compressor {
  def compress(bs: ByteString): ByteString
  def decompress(bs: ByteString): ByteString
}
