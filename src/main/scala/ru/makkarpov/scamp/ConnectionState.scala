package ru.makkarpov.scamp

import akka.util.ByteString

case class ConnectionState(readPackets: PacketSerializer[Packet], writePackets: PacketSerializer[Packet]) {
  def write(packet: Packet): ByteString = writePackets.write(packet)
  def read(data: ByteString): Packet = readPackets.read(data)
}
