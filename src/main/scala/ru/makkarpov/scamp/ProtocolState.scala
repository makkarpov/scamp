package ru.makkarpov.scamp

object ProtocolState {

}

abstract class ProtocolState {
  /* `write` for this serializer is a server -> client direction */
  val serverPackets: PacketSerializer[Packet]

  /* `write` for this serializer is a client -> server direction */
  val clientPackets: PacketSerializer[Packet]

  val serverState = ConnectionState(clientPackets, serverPackets)
  val clientState = ConnectionState(serverPackets, clientPackets)

  def toState(isServer: Boolean): ConnectionState = if (isServer) serverState else clientState
}
