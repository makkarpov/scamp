package ru.makkarpov.scamp.status.client

import ru.makkarpov.scamp.status.StatusPacket

case class StatusResponse(json: String) extends StatusPacket
