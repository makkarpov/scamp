package ru.makkarpov.scamp.macroz.types

import akka.util.{ByteIterator, ByteString, ByteStringBuilder}
import ru.makkarpov.scamp.macroz.ScalarTypeHandler

import scala.reflect.macros.blackbox

class LongTypeHandler extends ScalarTypeHandler {
  override def appliesTo(c: blackbox.Context)(tpe: c.Type): Boolean = tpe weak_<:< c.universe.typeOf[Long]

  override def generateReader(c: blackbox.Context)(src: c.Tree): c.Tree = {
    import c.universe._

    q"$src.getLong(_root_.java.nio.ByteOrder.BIG_ENDIAN)"
  }

  override def generateWriter(c: blackbox.Context)(data: c.Tree, dst: c.Tree): c.Tree = {
    import c.universe._

    q"$dst.putLong($data)(_root_.java.nio.ByteOrder.BIG_ENDIAN)"
  }

  override def toString: String = "long"
}
