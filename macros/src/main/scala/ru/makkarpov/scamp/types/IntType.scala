package ru.makkarpov.scamp.types

import ru.makkarpov.scamp.macroz.ByteOrderedType

import scala.reflect.macros.blackbox

class IntType(x: blackbox.Context)(rawArgs: Seq[blackbox.Context#Tree]) extends ByteOrderedType(x)(rawArgs) {
  import c.universe._

  override def appliesTo(tpe: c.universe.Type): Boolean = typeOf[Int] weak_<:< tpe

  override def makeReader(src: c.universe.Tree): c.universe.Tree = q"$src.getInt($byteOrder)"

  override def makeWriter(dst: c.universe.Tree, data: c.universe.Tree): c.universe.Tree =
    q"$dst.putInt($data.toInt)($byteOrder)"
}
