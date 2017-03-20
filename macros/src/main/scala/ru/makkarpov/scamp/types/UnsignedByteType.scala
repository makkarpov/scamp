package ru.makkarpov.scamp.types

import ru.makkarpov.scamp.macroz.TypeProvider

import scala.reflect.macros.blackbox

class UnsignedByteType(x: blackbox.Context)(rawArgs: Seq[blackbox.Context#Tree]) extends TypeProvider(x)(rawArgs) {
  import c.universe._

  override def appliesTo(tpe: c.universe.Type): Boolean = typeOf[Short] weak_<:< tpe

  override def makeReader(src: c.universe.Tree): c.universe.Tree = q"($src.getByte & 0xFF).toShort"

  override def makeWriter(dst: c.universe.Tree, data: c.universe.Tree): c.universe.Tree = q"$dst.putByte($data.toByte)"
}
