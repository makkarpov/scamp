package ru.makkarpov.scamp

import java.io.IOException
import java.nio.charset.StandardCharsets

import akka.util.{ByteIterator, ByteString, ByteStringBuilder}

object CommonFormats {
  def readString(src: ByteIterator, maxLength: Int = Short.MaxValue): String = {
    val len = VarIntUtils.readVarInt(src)

    if (len > maxLength)
      throw new IOException(s"Received too long string ($len > $maxLength)")

    if (len < 0)
      throw new IOException(s"Received string with negative length ($len < 0)")

    val buf = new Array[Byte](len)
    src.getBytes(buf)

    new String(buf, StandardCharsets.UTF_8)
  }

  def writeString(dst: ByteStringBuilder, s: String, maxLength: Int = Short.MaxValue): Unit = {
    val buf = s.getBytes(StandardCharsets.UTF_8)

    if (buf.length > maxLength)
      throw new IOException(s"Won't write too long string (${buf.length} > $maxLength)")

    VarIntUtils.writeVarInt(dst, buf.length)
    dst.putBytes(buf)
  }

  def readByteString(src: ByteIterator, maxLength: Int = Short.MaxValue): ByteString = {
    val len = VarIntUtils.readVarInt(src)

    if (len > maxLength)
      throw new IOException(s"Received too long ByteString ($len > $maxLength)")

    if (len < 0)
      throw new IOException(s"Received byte string with negative length ($len < 0)")

    src.getByteString(len)
  }

  def writeByteString(dst: ByteStringBuilder, s: ByteString, maxLength: Int = Short.MaxValue): Unit = {
    if (s.length > maxLength)
      throw new IOException(s"Won't write too long ByteString (${s.length} > $maxLength)")

    VarIntUtils.writeVarInt(dst, s.length)
    dst ++= s
  }
}
