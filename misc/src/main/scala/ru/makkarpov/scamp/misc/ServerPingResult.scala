package ru.makkarpov.scamp.misc

object ServerPingResult {
  case object Failure extends ServerPingResult

  case class Success(json: String, ping: Int) extends ServerPingResult
}

sealed trait ServerPingResult