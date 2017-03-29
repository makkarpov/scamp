package ru.makkarpov.scamp.types

import ru.makkarpov.scamp.macroz.{ByteOrderedType, TypeProvider}

import scala.reflect.macros.blackbox

class VarIntType(x: blackbox.Context)(rawArgs: Seq[blackbox.Context#Tree]) extends TypeProvider(x)(rawArgs) {
  import c.universe._

  val varIntUtils = q"_root_.ru.makkarpov.scamp.VarIntUtils"

  override def appliesTo(tpe: c.universe.Type): Boolean = typeOf[Int] weak_<:< tpe

  override def makeReader(src: c.universe.Tree): c.universe.Tree = q"$varIntUtils.readVarInt($src)"

  override def makeWriter(dst: c.universe.Tree, data: c.universe.Tree): c.universe.Tree =
    q"$varIntUtils.writeVarInt($dst, $data)"
}
