package ru.makkarpov.scamp.types

import ru.makkarpov.scamp.macroz.TypeProvider

import scala.reflect.macros.blackbox

class EnumerationType(x: blackbox.Context)(rawArgs: Seq[blackbox.Context#Tree]) extends TypeProvider(x)(rawArgs) {
  import c.universe._

  val enumInstance: Tree = args.head
  val valueType: Type = c.typecheck(q"$enumInstance.values.head").tpe
  val ownPkg = q"_root_.ru.makkarpov.scamp"

  override def appliesTo(tpe: c.universe.Type): Boolean = tpe <:< valueType

  // TODO: Non-varint enumerations
  override def makeReader(src: c.universe.Tree): c.universe.Tree =
    q"$enumInstance($ownPkg.VarIntUtils.readVarInt($src))"

  override def makeWriter(dst: c.universe.Tree, data: c.universe.Tree): c.universe.Tree =
    q"$ownPkg.VarIntUtils.writeVarLong($dst, $data.id)"
}
