package ru.makkarpov.scamp.actor

import akka.io.Tcp.Event
import akka.util.Timeout

import scala.concurrent.duration.FiniteDuration

/**
 * Creation date: 23.03.2017
 * Copyright (c) harati
 */
object InternalMessage {

  sealed trait InternalMessage
  case class Preload(timeout: FiniteDuration) extends InternalMessage
  case object Timeout extends InternalMessage

  case object Close extends InternalMessage

  case object Request extends Event

}
