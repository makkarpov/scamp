package ru.makkarpov.scamp

import akka.util.{ByteIterator, ByteString, ByteStringBuilder}

object VarIntUtils {
  def readVarInt(source: ByteIterator): Int = {
    val l = readVarLong(source)

    if (l.toInt != l)
      throw new IllegalArgumentException("VarInt is out of range")

    l.toInt
  }

  def readVarLong(source: ByteIterator): Long = {
    var ret = 0L
    var buf: Byte = 0
    var pos = 0

    do {
      buf = source.getByte
      ret |= (buf & 0x7F).toLong << pos
      pos += 7
    } while ((buf & 0x80) != 0)

    ret
  }

  def varIntLength(f: Long): Int = {
    if (f == 0) return 1

    var r = 0
    var l = f
    while (l != 0) {
      r += 1
      l = l >>> 7
    }

    r
  }

  def writeVarInt(dst: ByteStringBuilder, i: Int): Unit = {
    if (i == 0) dst.putByte(0)

    var data = i
    while (data != 0) {
      if ((data & ~0x7F) != 0) dst.putByte((0x80 | (data & 0x7F)).toByte)
      else dst.putByte(data.toByte)

      data >>>= 7
    }
  }

  def writeVarLong(dst: ByteStringBuilder, l: Long): Unit = {
    if (l == 0) dst.putByte(0)

    var data = l
    while (data != 0) {
      if ((data & ~0x7FL) != 0) dst.putByte((0x80 | (data & 0x7F)).toByte)
      else dst.putByte(data.toByte)

      data >>>= 7
    }
  }

  def decodeVarInt(source: ByteString, offset: Int): Option[Long] = {
    var r = 0L
    var pos = 0

    while (true) {
      if (offset + pos >= source.length) return None

      val b = source.apply(offset + pos)
      r |= (b & 0x7F).toLong << (7 * pos)
      pos += 1

      if ((b & 0x80) == 0)
        return Some(r)
    }

    throw new IllegalStateException("Unreachable code, don't you see?")
  }
}