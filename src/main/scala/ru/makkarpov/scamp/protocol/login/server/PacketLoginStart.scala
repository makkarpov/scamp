package ru.makkarpov.scamp.protocol.login.server

import ru.makkarpov.scamp.protocol.login.LoginPacket

case class PacketLoginStart(name: String) extends LoginPacket
