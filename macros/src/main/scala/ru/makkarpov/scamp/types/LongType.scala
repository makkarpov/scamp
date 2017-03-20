package ru.makkarpov.scamp.types

import ru.makkarpov.scamp.macroz.ByteOrderedType

import scala.reflect.macros.blackbox

class LongType(x: blackbox.Context)(rawArgs: Seq[blackbox.Context#Tree]) extends ByteOrderedType(x)(rawArgs) {
  import c.universe._

  override def appliesTo(tpe: c.universe.Type): Boolean = tpe =:= typeOf[Long]

  override def makeReader(src: c.universe.Tree): c.universe.Tree = q"$src.getLong($byteOrder)"

  override def makeWriter(dst: c.universe.Tree, data: c.universe.Tree): c.universe.Tree =
    q"$dst.putLong($data)($byteOrder)"
}
