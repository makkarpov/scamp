package ru.makkarpov.scamp.status.server

import ru.makkarpov.scamp.status.StatusPacket

case class StatusPing(payload: Long) extends StatusPacket
