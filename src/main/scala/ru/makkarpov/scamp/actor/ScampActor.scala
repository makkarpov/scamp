package ru.makkarpov.scamp.actor

import akka.actor.{Actor, ActorRef, Terminated}
import akka.io.Tcp.Received
import ru.makkarpov.scamp.actor.InternalMessage.{Close, Request}
import ru.makkarpov.scamp.{CipherSet, ConnectionState, Packet, ProtocolState}
import ru.makkarpov.scamp.handshake.HandshakingState

import scala.util.{Failure, Success, Try}

/**
 * Creation date: 23.03.2017
 * Copyright (c) harati
 */
abstract class ScampActor(out: ActorRef) extends Actor {

  private[actor] implicit var state: ProtocolState = HandshakingState
  private[actor] var packet: PacketSender = PacketSender(out, Request)
  private[actor] var cipher: CipherSet = CipherSet.empty
  private[actor] var codec = Codec.empty

  override def preStart(): Unit = {
    context.watch(out)
  }

  override def postStop(): Unit = {
    context.unwatch(out)
  }

  private[actor] def isServer: Boolean
  private[actor] val income: PartialFunction[Packet, Any]

  /**
   * Send packet away
   */
  def away(p: Packet): Unit = try packet = packet(p) finally p match {
    case f: onSendSwitch => f.onSend(this).foreach(state = _)
    case _               =>
  }

  def receive = {
    case Received(data) => codec(cipher.decrypt(data)) match {
      case (newer, packets) =>
        codec = newer
        packets.reverse foreach {
          case (id, body) => Try(state.toState(isServer).read(id, body.toByteString)) match {
            case Success(p) => try income(p) finally p match {
              case f: onReceiveSwitch => f.onReceive(this).foreach(state = _)
              case _                  =>
            }
            case Failure(f) => //TODO: Some error reporting
          }
        }
    }
    case Request          => packet = packet.next
    case Close            => context stop self
    case Terminated(some) => if (out == some) context stop self
  }

  implicit def expose: ScampActor = this

}
