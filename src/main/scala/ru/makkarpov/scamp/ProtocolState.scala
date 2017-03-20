package ru.makkarpov.scamp

import ru.makkarpov.scamp.handshake.HandshakingState

object ProtocolState {
  val Handshaking: ProtocolState = HandshakingState
}

abstract class ProtocolState {
  /* `write` for this serializer is a server -> client direction */
  val serverPackets: PacketSerializer[Packet]

  /* `write` for this serializer is a client -> server direction */
  val clientPackets: PacketSerializer[Packet]

  lazy val serverState = ConnectionState(clientPackets, serverPackets)
  lazy val clientState = ConnectionState(serverPackets, clientPackets)

  def toState(isServer: Boolean): ConnectionState = if (isServer) serverState else clientState
}
