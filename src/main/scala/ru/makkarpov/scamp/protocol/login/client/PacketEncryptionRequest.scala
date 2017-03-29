package ru.makkarpov.scamp.protocol.login.client

import akka.util.ByteString
import ru.makkarpov.scamp.protocol.login.LoginPacket

case class PacketEncryptionRequest(serverId: String, publicKey: ByteString, verifyToken: ByteString) extends LoginPacket
