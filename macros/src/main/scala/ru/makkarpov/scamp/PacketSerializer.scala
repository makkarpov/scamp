package ru.makkarpov.scamp

import java.nio.ByteOrder

import akka.util.{ByteString, ByteStringBuilder}

import scala.language.existentials

object PacketSerializer {
  implicit val byteOrder = ByteOrder.BIG_ENDIAN

  abstract class Default[T] extends PacketSerializer[T] {
    def packetId(packet: T): Int
    def packetSerializer(id: Int): InternalSerializer[_ <: T]

    override def write(packet: T): (Int, ByteString) = {
      val id = packetId(packet)
      val dst = new ByteStringBuilder
      packetSerializer(id).asInstanceOf[InternalSerializer[T]].write(packet, dst)
      id -> dst.result()
    }

    override def read(id: Int, src: ByteString): T = packetSerializer(id).read(src.iterator)
  }
}

trait PacketSerializer[T] {
  def write(packet: T): (Int, ByteString)
  def read(id: Int, src: ByteString): T
}
