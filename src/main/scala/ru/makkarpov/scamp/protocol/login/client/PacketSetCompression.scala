package ru.makkarpov.scamp.protocol.login.client

import ru.makkarpov.scamp.protocol.login.LoginPacket

case class PacketSetCompression(threshold: Int) extends LoginPacket
