package ru.makkarpov.scamp.handshake

import ru.makkarpov.scamp.{Packet, PacketSerializer, ProtocolState}

object HandshakingState extends ProtocolState {
  import ru.makkarpov.scamp.ProtocolDef._
  import ru.makkarpov.scamp.Types._

  override val serverPackets: PacketSerializer[Packet] = protocol(
    0 -> packet[PacketHandshake](
      "protocolVersion" -> varInt,
      "serverAddress" -> string(maxLength = 64),
      "serverPort" -> unsignedShort,
      "nextState" -> enum(PacketHandshake.NextState)
    )
  )

  override val clientPackets: PacketSerializer[Packet] = protocol()
}
