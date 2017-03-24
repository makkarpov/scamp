package ru.makkarpov.scamp

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorRefFactory, Props}
import ru.makkarpov.scamp.actor.{ScampClient, ScampServer}
import akka.pattern.ask
import akka.util.Timeout
import ru.makkarpov.scamp.actor.InternalMessage.Close

import scala.concurrent.Future
import scala.concurrent.duration._
import akka.pattern.gracefulStop

/**
 * Creation date: 23.03.2017
 * Copyright (c) harati
 */
object Scamp {

  protected implicit val timeout: FiniteDuration = 5 seconds

  def server(point: InetSocketAddress)(implicit factory: ActorRefFactory, timeout: FiniteDuration): Future[ServerRef] =
    ScampServer.create(point, timeout) map { ref =>
      new ServerRef {
        override def close: Future[_] = gracefulStop(ref, timeout, Close)
      }
    }

}
