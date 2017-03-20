package ru.makkarpov.scamp.types

import ru.makkarpov.scamp.macroz.{ByteOrderedType, TypeProvider}

import scala.reflect.macros.blackbox

class ShortType(x: blackbox.Context)(rawArgs: Seq[blackbox.Context#Tree]) extends ByteOrderedType(x)(rawArgs) {
  import c.universe._

  override def appliesTo(tpe: c.universe.Type): Boolean = typeOf[Short] weak_<:< tpe

  override def makeReader(src: c.universe.Tree): c.universe.Tree = q"$src.getShort($byteOrder)"

  override def makeWriter(dst: c.universe.Tree, data: c.universe.Tree): c.universe.Tree =
    q"$dst.putShort($data.toShort)($byteOrder)"
}
