package ru.makkarpov.scamp.protocol.game

import ru.makkarpov.scamp.{Packet, PacketSerializer, ProtocolState}

object GameState extends ProtocolState {
  import ru.makkarpov.scamp.ProtocolDef._
  import ru.makkarpov.scamp.Types._

  override val serverPackets: PacketSerializer[Packet] = protocol()
  override val clientPackets: PacketSerializer[Packet] = protocol()
}
