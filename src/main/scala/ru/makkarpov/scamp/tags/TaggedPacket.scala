package ru.makkarpov.scamp.tags

import ru.makkarpov.scamp.Packet

case class TaggedPacket(packet: Packet, tags: Seq[PacketTag]) extends Packet {

}
