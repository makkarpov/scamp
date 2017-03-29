package ru.makkarpov.scamp.protocol.status.client

import ru.makkarpov.scamp.protocol.status.StatusPacket

case class StatusResponse(json: String) extends StatusPacket
