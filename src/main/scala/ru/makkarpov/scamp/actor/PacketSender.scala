package ru.makkarpov.scamp.actor

import akka.actor.{ActorContext, ActorRef}
import akka.io.Tcp.Event
import akka.io.TcpMessage
import akka.util.ByteStringBuilder
import ru.makkarpov.scamp.{CipherSet, Packet, ProtocolState}

import scala.collection.immutable.Queue
import ru.makkarpov.scamp.VarIntUtils._

/**
 * Creation date: 26.03.2016
 * Copyright (c) harati
 */
case class PacketSender(out: ActorRef, request: Event, queue: Queue[Packet] = Queue.empty, notSending: Boolean = true) {

  def apply(packet: Packet)(implicit context: ScampActor): PacketSender = if (notSending) {
    val builder = new ByteStringBuilder
    val (id, body) = context.state.toState(context.isServer).write(packet)
    writeVarLong(builder, id)
    builder ++= body
    out.tell(TcpMessage.write(context.cipher.encrypt(builder.result), request), context.self)
    copy(notSending = false)
  } else copy(queue = queue :+ packet)

  def next(implicit context: ScampActor) = queue.dequeueOption match {
    case None                 ⇒ copy(notSending = true)
    case Some((packet, rest)) ⇒ copy(queue = rest, notSending = true).apply(packet)
  }

}
