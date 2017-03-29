package ru.makkarpov.scamp

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.scaladsl.Tcp.OutgoingConnection
import akka.stream.scaladsl.{Flow, Tcp}
import ru.makkarpov.scamp.net.{PacketEncoder, PacketFramer}

import scala.concurrent.Future
import scala.concurrent.duration.Duration

object Scamp {
  def connect(address: InetSocketAddress, timeout: Duration = Duration.Inf)
             (implicit sys: ActorSystem): Flow[Packet, Packet, Future[OutgoingConnection]] =
    Tcp().outgoingConnection(address, connectTimeout = timeout).join(new PacketFramer).join(new PacketEncoder(false))
}
