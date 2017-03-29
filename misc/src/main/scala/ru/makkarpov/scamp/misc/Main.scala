package ru.makkarpov.scamp.misc

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object Main extends App {
  implicit val system = ActorSystem("pinger")
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  ServerPinger.ping(new InetSocketAddress("alpha.srv.hil.su", 25100)).onComplete(println)
}
