package ru.makkarpov.scamp.actor

import akka.actor.{ActorRef, ActorRefFactory, Props}

/**
 * Creation date: 23.03.2017
 * Copyright (c) harati
 */
object ScampServerConnect {
  def create(out: ActorRef)(implicit factory: ActorRefFactory) = factory.actorOf(Props(classOf[ScampServerConnect], out))
}

class ScampServerConnect(out: ActorRef) extends ScampActor(out) {
  override private[actor] def isServer = true

  override private[actor] val income = {
    case x => println("meow meow " + x)
  }
}
