package ru.makkarpov.scamp.macroz.types

import ru.makkarpov.scamp.macroz.ScalarTypeHandler

import scala.reflect.macros.blackbox

class VarLongTypeHandler extends ScalarTypeHandler {
  override def appliesTo(c: blackbox.Context)(tpe: c.Type): Boolean = tpe weak_<:< c.universe.typeOf[Long]

  override def generateReader(c: blackbox.Context)(src: c.Tree): c.Tree = {
    import c.universe._

    q"_root_.ru.makkarpov.scamp.VarIntUtils.readVarLong($src)"
  }

  override def generateWriter(c: blackbox.Context)(data: c.Tree, dst: c.Tree): c.Tree = {
    import c.universe._

    q"_root_.ru.makkarpov.scamp.VarIntUtils.writeVarLong($dst, $data)"
  }

  override def toString: String = "varlong"
}
