package ru.makkarpov.scamp

import akka.util.{ByteIterator, ByteStringBuilder}

trait InternalSerializer[T] {
  def read(src: ByteIterator): T
  def write(packet: T, dst: ByteStringBuilder): Unit
}
