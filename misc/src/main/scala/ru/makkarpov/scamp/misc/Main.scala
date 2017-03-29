package ru.makkarpov.scamp.misc

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import ru.makkarpov.scamp.Scamp

import scala.concurrent.duration._

object Main extends App {
  implicit val system = ActorSystem("pinger")
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val addr = new InetSocketAddress("123.123.123.123", 25565)
  ServerPinger.ping(Scamp.client(addr), addr).onComplete(println)
}
