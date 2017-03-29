package ru.makkarpov.scamp.protocol.login

import ru.makkarpov.scamp.protocol.login.client.{PacketDisconnect, PacketEncryptionRequest, PacketLoginSuccess, PacketSetCompression}
import ru.makkarpov.scamp.protocol.login.server.{PacketEncryptionResponse, PacketLoginStart}
import ru.makkarpov.scamp.{Packet, PacketSerializer, ProtocolState}

object LoginState extends ProtocolState {
  import ru.makkarpov.scamp.ProtocolDef._
  import ru.makkarpov.scamp.Types._

  override val serverPackets: PacketSerializer[Packet] = protocol(
    0 -> packet[PacketLoginStart](
      "name"        -> string(maxLength = 16)
    ),
    1 -> packet[PacketEncryptionResponse](
      "sharedSecret"  -> byteString(),
      "verifyToken"   -> byteString()
    )
  )

  override val clientPackets: PacketSerializer[Packet] = protocol(
    0 -> packet[PacketDisconnect](
      "reason"      -> string()
    ),
    1 -> packet[PacketEncryptionRequest](
      "serverId"    -> string(maxLength = 20),
      "publicKey"   -> byteString(),
      "verifyToken" -> byteString()
    ),
    2 -> packet[PacketLoginSuccess](
      "uuid"        -> string(maxLength = 36),
      "username"    -> string(maxLength = 16)
    ),
    3 -> packet[PacketSetCompression](
      "threshold"   -> varInt
    )
  )
}
