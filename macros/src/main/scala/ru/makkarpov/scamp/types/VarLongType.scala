package ru.makkarpov.scamp.types

import ru.makkarpov.scamp.macroz.TypeProvider

import scala.reflect.macros.blackbox

class VarLongType(x: blackbox.Context)(rawArgs: Seq[blackbox.Context#Tree]) extends TypeProvider(x)(rawArgs) {
  import c.universe._

  val varIntUtils = q"_root_.ru.makkarpov.scamp.VarIntUtils"

  override def appliesTo(tpe: c.universe.Type): Boolean = tpe =:= typeOf[Long]

  override def makeReader(src: c.universe.Tree): c.universe.Tree = q"$varIntUtils.readVarLong($src)"

  override def makeWriter(dst: c.universe.Tree, data: c.universe.Tree): c.universe.Tree =
    q"$varIntUtils.writeVarLong($dst, $data)"
}
