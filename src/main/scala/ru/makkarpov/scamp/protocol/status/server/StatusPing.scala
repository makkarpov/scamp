package ru.makkarpov.scamp.protocol.status.server

import ru.makkarpov.scamp.protocol.status.StatusPacket

case class StatusPing(payload: Long) extends StatusPacket
