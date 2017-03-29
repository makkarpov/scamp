package ru.makkarpov.scamp.misc

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.duration._

object Main extends App {
  implicit val system = ActorSystem("pinger")
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  ServerPinger.ping(new InetSocketAddress("localhost", 25100), readTimeout = 10 second span).onComplete(println)
}
