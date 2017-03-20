package ru.makkarpov.scamp.types

import ru.makkarpov.scamp.macroz.TypeProvider

import scala.reflect.macros.blackbox

class ByteType(x: blackbox.Context)(rawArgs: Seq[blackbox.Context#Tree]) extends TypeProvider(x)(rawArgs) {
  import c.universe._

  override def appliesTo(tpe: Type): Boolean = typeOf[Byte] weak_<:< tpe
  override def makeReader(src: Tree): Tree = q"$src.getByte"
  override def makeWriter(dst: Tree, data: Tree): Tree = q"$dst.putByte($data.toByte)"
}
