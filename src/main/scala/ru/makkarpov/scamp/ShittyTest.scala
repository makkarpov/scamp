package ru.makkarpov.scamp

import ru.makkarpov.scamp.Packet.{FirstPacket, SecondPacket, ThirdPacket}

class ShittyTest {
  import ProtocolDef._
  import Types._
  import GenerateProtocol.protocol

  val r =
    protocol[Packet](
      0 -> packet(
        selector(string)(
          "shit1" -> packet[FirstPacket]( string ),
          "shit2" -> packet[SecondPacket]( long ),
          defaultNamed("ch") -> packet[ThirdPacket] ( "msg" -> rawBytes )
        )
      )
    )
}
