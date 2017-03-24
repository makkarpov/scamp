package ru.makkarpov.scamp.actor

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorRefFactory, Props}

/**
 * Creation date: 23.03.2017
 * Copyright (c) harati
 */
object ScampClient {
  def create(point: InetSocketAddress, out: ActorRef)(implicit factory: ActorRefFactory) = factory.actorOf(Props(classOf[ScampClient], point, out))
}
class ScampClient(point: InetSocketAddress, out: ActorRef) extends ScampActor(out) {
  override private[actor] def isServer = false
}
