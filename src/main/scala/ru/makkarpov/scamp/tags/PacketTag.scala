package ru.makkarpov.scamp.tags

import akka.util.ByteString

object PacketTag {
  case class EnableCompression(threshold: Int, level: Int) extends PacketTag
  case class EnableEncryption(key: ByteString) extends PacketTag
}

sealed trait PacketTag {

}
