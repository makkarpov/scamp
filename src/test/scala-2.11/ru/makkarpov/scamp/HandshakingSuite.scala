package ru.makkarpov.scamp

import java.io.IOException
import java.nio.charset.StandardCharsets

import akka.util.ByteString
import org.scalatest.{FlatSpec, Matchers}
import ru.makkarpov.scamp.handshake.PacketHandshake
import ru.makkarpov.scamp.handshake.PacketHandshake.NextState

class HandshakingSuite extends FlatSpec with Matchers {
  it must "read handshake packets" in {
    val state = ProtocolState.Handshaking.serverState

    state.read(0, ByteString(
      47, 9, '1', '2', '7', '.', '0', '.', '0', '.', '1', 0x63, 0xdd, 0x02
    )) shouldBe PacketHandshake(47, "127.0.0.1", 25565, NextState.Login)

    state.read(0, ByteString(
      47, 9, 'l', 'o', 'c', 'a', 'l', 'h', 'o', 's', 't', 0x63, 0xdd, 0x01
    )) shouldBe PacketHandshake(47, "localhost", 25565, NextState.Status)

    val notLongString = ByteString(("x" * 64).getBytes(StandardCharsets.UTF_8))
    state.read(0, ByteString(47, 64) ++ notLongString ++ ByteString(0x63, 0xdd, 0x01)) shouldBe
      PacketHandshake(47, "x" * 64, 25565, NextState.Status)

    a [NoSuchElementException] shouldBe thrownBy {
      state.read(0, ByteString(
        47, 9, 'l', 'o', 'c', 'a', 'l', 'h', 'o', 's', 't', 0x63, 0xdd, 0x03 // invalid next state
      ))
    }

    an [IOException] shouldBe thrownBy {
      val longString = ByteString(new Array[Byte](65))
      state.read(0, ByteString(47, 65) ++ longString ++ ByteString(0x63, 0xdd, 0x01))
    }
  }

  it must "write handshake packets" in {
    val state = ProtocolState.Handshaking.clientState

    state.write(PacketHandshake(47, "127.0.0.1", 25565, NextState.Login)) shouldBe
      (0, ByteString(47, 9, '1', '2', '7', '.', '0', '.', '0', '.', '1', 0x63, 0xdd, 0x02))

    an [IOException] shouldBe thrownBy {
      state.write(PacketHandshake(47, "x" * 65, 25565, NextState.Login))
    }
  }
}
