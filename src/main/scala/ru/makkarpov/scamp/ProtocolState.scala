package ru.makkarpov.scamp

import ru.makkarpov.scamp.protocol.game.GameState
import ru.makkarpov.scamp.protocol.handshake.HandshakingState
import ru.makkarpov.scamp.protocol.login.LoginState
import ru.makkarpov.scamp.protocol.status.StatusState

object ProtocolState {
  val Handshaking: ProtocolState = HandshakingState
  val Status: ProtocolState = StatusState
  val Login: ProtocolState = LoginState
  val Game: ProtocolState = GameState
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
