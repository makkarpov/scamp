package ru.makkarpov.scamp.net

import akka.util.ByteString
import ru.makkarpov.scamp.VarIntUtils._
import ru.makkarpov.scamp.net.Framer.ReadPhase

case object Framer {
  object ReadPhase extends Enumeration {
    val Length, Data = Value
  }

  def empty = new Framer()
}

/**
 * Creation date: 22.10.2015
 * Copyright (c) harati
 */
case class Framer(
  phase:          ReadPhase.Value = ReadPhase.Length,
  buffer:         ByteString      = ByteString.empty,
  expectedLength: Int             = 0
) {
  def parse(phase: ReadPhase.Value, f: ByteString, expectedLength: Int): (Framer, List[ByteString]) = {
    def empty = (Framer(phase, f, expectedLength), List.empty[ByteString])
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

  def apply(f: ByteString): (Framer, List[ByteString]) = parse(phase, buffer concat f, expectedLength)
}
