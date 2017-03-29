package ru.makkarpov.scamp

import java.net.InetSocketAddress

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Tcp}
import ru.makkarpov.scamp.cipher.CipherStage
import ru.makkarpov.scamp.net.{EncodingStage, FramingStage}

import scala.concurrent.duration._

object Scamp {
  def client(remoteAddr: InetSocketAddress, connectTimeout: FiniteDuration = 10 second span)
            (implicit sys: ActorSystem): Flow[Packet, Packet, NotUsed] =
    Tcp().outgoingConnection(
      remoteAddress = remoteAddr,
      connectTimeout = connectTimeout,
      halfClose = false
    ).join(CipherStage).join(new FramingStage).join(EncodingStage.Client)
      .mapMaterializedValue(_ => NotUsed)

}
