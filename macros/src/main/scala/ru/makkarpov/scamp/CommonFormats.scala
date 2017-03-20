package ru.makkarpov.scamp

import java.io.IOException
import java.nio.charset.StandardCharsets

import akka.util.{ByteIterator, ByteStringBuilder}

object CommonFormats {
  def readString(src: ByteIterator, maxLength: Int = Short.MaxValue): String = {
    val len = VarIntUtils.readVarInt(src)

    if (len > maxLength)
      throw new IOException(s"Received too long string ($len > $maxLength)")

    val buf = new Array[Byte](len)
    src.getBytes(buf)

    new String(buf, StandardCharsets.UTF_8)
  }

  def writeString(dst: ByteStringBuilder, s: String, maxLength: Int = Short.MaxValue): Unit = {
    val buf = s.getBytes(StandardCharsets.UTF_8)

    if (buf.length > maxLength)
      throw new IOException(s"Won't write too long string (${buf.length} > $maxLength)")

    VarIntUtils.writeVarLong(dst, buf.length)
    dst.putBytes(buf)
  }
}
