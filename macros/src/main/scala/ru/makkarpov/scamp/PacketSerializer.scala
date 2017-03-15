package ru.makkarpov.scamp

import java.nio.ByteOrder

import akka.util.{ByteString, ByteStringBuilder}

import scala.language.existentials

object PacketSerializer {
  implicit val byteOrder = ByteOrder.BIG_ENDIAN

  abstract class Default[T] extends PacketSerializer[T] {
    type Ser = InternalSerializer[_ <: T]

    def forPacket(packet: T): Ser
    def forId(id: Int): Ser

    override def write(packet: T): ByteString = {
      val dst = new ByteStringBuilder
      forPacket(packet).asInstanceOf[InternalSerializer[T]].write(packet, dst)
      dst.result()
    }

    override def read(src: ByteString): T = {
      val iter = src.iterator
      forId(iter.getInt).read(iter)
    }
  }
}

trait PacketSerializer[T] {
  def write(packet: T): ByteString
  def read(src: ByteString): T
}
