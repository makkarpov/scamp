package ru.makkarpov.scamp.net

import akka.util.ByteString
import ru.makkarpov.scamp.VarIntUtils._
import ru.makkarpov.scamp.net.Codec.ReadPhase

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
  def parse(phase: ReadPhase.Value, f: ByteString, expectedLength: Int): (Codec, List[ByteString]) = {
    def empty = (Codec(phase, f, expectedLength), List.empty[ByteString])
    phase match {
      case ReadPhase.Length ⇒ decodeVarInt(f, 0).map(_.toInt) match {
        case None         ⇒ empty
        case Some(length) ⇒ parse(ReadPhase.Data, f.slice(varIntLength(length), f.length), length)
      }
      case ReadPhase.Data ⇒ if (f.length >= expectedLength) {
        val data = f.slice(0, expectedLength)
        val future = f.slice(expectedLength, f.length)
        parse(ReadPhase.Length, future, f.length) match {
          case (codec, Nil)  ⇒ (codec, List(data))
          case (codec, list) ⇒ (codec, list ++ List(data))
        }
      } else empty
    }
  }

  def apply(f: ByteString): (Codec, List[ByteString]) = parse(phase, buffer concat f, expectedLength)
}
