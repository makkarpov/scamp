package ru.makkarpov.scamp.actor

import akka.util.{ByteIterator, ByteString}
import ru.makkarpov.scamp.actor.Codec.ReadPhase
import ru.makkarpov.scamp.VarIntUtils._

case object Codec {

  object ReadPhase extends Enumeration {
    val Length, Data = Value
  }

  def empty = new Codec()

}

/**
 * Creation date: 22.10.2015
 * Copyright (c) harati
 */
case class Codec(
  phase:          ReadPhase.Value = ReadPhase.Length,
  buffer:         ByteString      = ByteString.empty,
  expectedLength: Int             = 0
) {

  type Decoded = (Int, ByteIterator)

  def parse(phase: ReadPhase.Value, f: ByteString, expectedLength: Int): (Codec, List[Decoded]) = {
    def empty = (Codec(phase, f, expectedLength), List.empty[Decoded])
    phase match {
      case ReadPhase.Length ⇒ decodeVarInt(f, 0).map(_.toInt) match {
        case None         ⇒ empty
        case Some(length) ⇒ parse(ReadPhase.Data, f.slice(varIntLength(length), f.length), length)
      }
      case ReadPhase.Data ⇒ if (f.length >= expectedLength) {
        val data = f.slice(0, expectedLength).iterator
        val id = readVarInt(data)
        val future = f.slice(expectedLength, f.length)
        parse(ReadPhase.Length, future, f.length) match {
          case (codec, Nil)  ⇒ (codec, List((id, data)))
          case (codec, list) ⇒ (codec, list ++ List((id, data)))
        }
      } else empty
    }
  }

  def apply(f: ByteString) = parse(phase, buffer concat f, expectedLength)

}
