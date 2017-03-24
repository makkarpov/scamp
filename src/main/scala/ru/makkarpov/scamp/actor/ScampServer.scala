package ru.makkarpov.scamp.actor

import java.net.InetSocketAddress

import akka.actor.Actor.Receive
import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorRef, ActorRefFactory, OneForOneStrategy, Props, Status}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import ru.makkarpov.scamp.actor.InternalMessage.Preload
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
 * Creation date: 23.03.2017
 * Copyright (c) harati
 */
object ScampServer {
  def create(point: InetSocketAddress, timeout: FiniteDuration)(implicit factory: ActorRefFactory): Future[ActorRef] = {
    val promise = Promise[ActorRef]()
    val actor = factory.actorOf(Props(classOf[ScampServer], point)) ? Preload(timeout) onComplete {
      case Success(_) => promise success actor
      case Failure(f) => promise failure f
    }
    promise.future
  }
}

class ScampServer(point: InetSocketAddress) extends Actor {

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
    case _ => Stop
  }

  def preload(request: ActorRef, timeout: FiniteDuration): Unit = {
    val timer = context.system.scheduler.scheduleOnce(timeout, self, Timeout)
    def fail(reason: Exception) = {
      if (!timer.isCancelled) timer.cancel()
      request ! Status.Failure(reason)
      context stop self
    }
    context become {
      case Bound(some: _) =>
        timer.cancel()
        request ! some
        context.become(receive)
      case CommandFailed(_: Bind) => fail(new IllegalStateException("Failed to bind"))
      case Timeout                => fail(new IllegalStateException("Timeout"))
    }
  }

  override def preStart: Unit = {
    IO(Tcp) ! Bind(self, point)
    context.become {
      case Preload(timeout) => preload(sender, timeout)
      case _                => throw new IllegalStateException //Oh, shi
    }
  }

  override def receive: Receive = {
    case Connected(_, _) => sender ! Register(ScampServerConnect.create(sender))
    case Timeout         => //Some race can be
  }
}
