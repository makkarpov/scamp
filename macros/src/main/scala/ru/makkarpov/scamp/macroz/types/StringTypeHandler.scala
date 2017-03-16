package ru.makkarpov.scamp.macroz.types

import ru.makkarpov.scamp.macroz.ScalarTypeHandler

import scala.reflect.macros.blackbox

class StringTypeHandler extends ScalarTypeHandler {
  override def appliesTo(c: blackbox.Context)(tpe: c.Type): Boolean = tpe <:< c.universe.typeOf[String]

  override def generateReader(c: blackbox.Context)(src: c.Tree): c.Tree = {
    import c.universe._

    q"_root_.ru.makkarpov.scamp.CommonFormats.readString($src)"
  }

  override def generateLimitedReader(c: blackbox.Context)(src: c.Tree, limit: Int): c.Tree = {
    import c.universe._

    q"_root_.ru.makkarpov.scamp.CommonFormats.readString($src, ${limit.toShort})"
  }

  override def generateWriter(c: blackbox.Context)(data: c.Tree, dst: c.Tree): c.Tree = {
    import c.universe._

    q"_root_.ru.makkarpov.scamp.CommonFormats.writeString($data, $dst)"
  }

  override def toString: String = "string"
}
