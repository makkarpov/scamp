package ru.makkarpov.scamp.status.client

import ru.makkarpov.scamp.status.StatusPacket

case class StatusPong(payload: Long) extends StatusPacket
