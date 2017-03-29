package ru.makkarpov.scamp.status

import ru.makkarpov.scamp.status.client.{StatusPong, StatusResponse}
import ru.makkarpov.scamp.status.server.{StatusPing, StatusRequest}
import ru.makkarpov.scamp.{Packet, PacketSerializer, ProtocolState}

object StatusState extends ProtocolState {
  import ru.makkarpov.scamp.ProtocolDef._
  import ru.makkarpov.scamp.Types._

  override val serverPackets: PacketSerializer[Packet] = protocol[Packet](
    0 -> packet[StatusRequest](),
    1 -> packet[StatusPing](
      "payload"   -> long
    )
  )

  override val clientPackets: PacketSerializer[Packet] = protocol[Packet](
    0 -> packet[StatusResponse](
      "json"      -> string(maxLength = 32768)
    ),
    1 -> packet[StatusPong](
      "payload"   -> long
    )
  )
}
