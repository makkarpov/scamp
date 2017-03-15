package ru.makkarpov.scamp

import akka.util.{ByteIterator, ByteString, ByteStringBuilder}

object CommonFormats {
  def readLong(src: ByteIterator): Long = ???
  def writeLong(l: Long, dst: ByteStringBuilder): Unit = ???

  def readString(src: ByteIterator): String = ???
  def writeString(s: String, dst: ByteStringBuilder): Unit = ???

  def readRawBytes(src: ByteIterator): ByteString = src.toByteString
  def writeRawBytes(data: ByteString, dst: ByteStringBuilder): Unit = dst ++= data
}
