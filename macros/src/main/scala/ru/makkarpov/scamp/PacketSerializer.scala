package ru.makkarpov.scamp

import java.nio.ByteOrder

import akka.util.{ByteString, ByteStringBuilder}

import scala.language.existentials

object PacketSerializer {
  implicit val byteOrder = ByteOrder.BIG_ENDIAN

  abstract class Default[T] extends PacketSerializer[T] {
    def packetId(packet: T): Int
    def packetSerializer(id: Int): InternalSerializer[_ <: T]

    override def write(packet: T): ByteString = {
      val id = packetId(packet)
      val dst = new ByteStringBuilder
      VarIntUtils.writeVarInt(dst, id)
      packetSerializer(id).asInstanceOf[InternalSerializer[T]].write(packet, dst)
      dst.result()
    }

    override def read(src: ByteString): T = {
      val iter = src.iterator
      val id = VarIntUtils.readVarInt(iter)
      packetSerializer(id).read(iter)
    }
  }
}

trait PacketSerializer[T] {
  def write(packet: T): ByteString
  def read(src: ByteString): T
}
