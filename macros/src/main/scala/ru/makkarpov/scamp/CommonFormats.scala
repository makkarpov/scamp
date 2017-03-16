package ru.makkarpov.scamp

import java.io.IOException
import java.nio.charset.StandardCharsets

import akka.util.{ByteIterator, ByteStringBuilder}

object CommonFormats {
  def readString(src: ByteIterator, maxLength: Short = Short.MaxValue): String = {
    val len = VarIntUtils.readVarInt(src)

    if (len > maxLength)
      throw new IOException("Received too long string")

    val buf = new Array[Byte](len)
    src.getBytes(buf)

    new String(buf, StandardCharsets.UTF_8)
  }

  def writeString(s: String, dst: ByteStringBuilder): Unit = {
    val buf = s.getBytes(StandardCharsets.UTF_8)

    VarIntUtils.writeVarLong(dst, buf.length)
    dst.putBytes(buf)
  }
}
