package ru.makkarpov.scamp

import akka.util.ByteString

object Packet {
  case class FirstPacket(x: String) extends Packet
  case class SecondPacket(x: Long) extends Packet
  case class ThirdPacket(ch: String, msg: ByteString) extends Packet
}

abstract class Packet {

}
