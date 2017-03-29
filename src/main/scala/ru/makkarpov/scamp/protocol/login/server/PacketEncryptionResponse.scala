package ru.makkarpov.scamp.protocol.login.server

import akka.util.ByteString
import ru.makkarpov.scamp.Packet

case class PacketEncryptionResponse(sharedSecret: ByteString, verifyToken: ByteString) extends Packet
