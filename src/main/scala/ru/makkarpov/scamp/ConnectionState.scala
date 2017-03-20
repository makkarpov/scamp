package ru.makkarpov.scamp

import akka.util.ByteString

case class ConnectionState(readPackets: PacketSerializer[Packet], writePackets: PacketSerializer[Packet]) {
  def write(packet: Packet): (Int, ByteString) = writePackets.write(packet)
  def read(id: Int, data: ByteString): Packet = readPackets.read(id, data)
}
