package ru.makkarpov.scamp.protocol.handshake

import ru.makkarpov.scamp.{Packet, ProtocolState}
import ru.makkarpov.scamp.protocol.handshake.PacketHandshake.NextState

object PacketHandshake {
  type NextState = NextState.Value
  object NextState extends Enumeration {
    val Status = Value(1, "status")
    val Login = Value(2, "login")

    def toProtocol(v: Value): ProtocolState = v match {
      case Status => ProtocolState.Status
      case Login => ProtocolState.Login
    }
  }
}

case class PacketHandshake(protocolVersion: Int, serverAddress: String, serverPort: Int, nextState: NextState)
extends Packet
