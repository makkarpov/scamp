package ru.makkarpov.scamp.protocol.login.client

import ru.makkarpov.scamp.protocol.login.LoginPacket

case class PacketLoginSuccess(uuid: String, username: String) extends LoginPacket
