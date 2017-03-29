package ru.makkarpov.scamp.net

import akka.NotUsed
import akka.stream.javadsl.BidiFlow
import ru.makkarpov.scamp.Packet

import scala.concurrent.duration.FiniteDuration

object TimeoutStage {
  def apply(timeout: FiniteDuration): BidiFlow[Packet, Packet, Packet, Packet, NotUsed] =
    BidiFlow.bidirectionalIdleTimeout(timeout)
}
