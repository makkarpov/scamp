package ru.makkarpov.scamp

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorRefFactory}
import ru.makkarpov.scamp.actor.InternalMessage.Close
import akka.pattern.ask

import scala.concurrent.Future
import akka.pattern.ask
import ru.makkarpov.scamp.actor.ScampServer

/**
 * Creation date: 23.03.2017
 * Copyright (c) harati
 */
abstract class AbstractRef {
  def close: Future[_]
}

abstract class ServerRef extends AbstractRef

abstract class ClientRef extends AbstractRef {
  def push(data: Packet): Unit
}

