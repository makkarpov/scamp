package ru.makkarpov.scamp

import ru.makkarpov.scamp.handshake.HandshakingState
import ru.makkarpov.scamp.status.StatusState

object ProtocolState {
  val Handshaking: ProtocolState = HandshakingState
  val Status: ProtocolState = StatusState
}

abstract class ProtocolState {
  /* packets that are sent to server, `Serverbound` in wiki.vg */
  val serverPackets: PacketSerializer[Packet]

  /* packets that are sent to client, `Clientbound` in wiki.vg */
  val clientPackets: PacketSerializer[Packet]

  lazy val serverState = ConnectionState(serverPackets, clientPackets)
  lazy val clientState = ConnectionState(clientPackets, serverPackets)

  def toState(isServer: Boolean): ConnectionState = if (isServer) serverState else clientState
}
