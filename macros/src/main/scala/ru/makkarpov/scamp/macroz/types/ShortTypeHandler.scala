package ru.makkarpov.scamp.macroz.types

import ru.makkarpov.scamp.macroz.ScalarTypeHandler

import scala.reflect.macros.blackbox

class ShortTypeHandler extends ScalarTypeHandler {
  override def appliesTo(c: blackbox.Context)(tpe: c.Type): Boolean = tpe weak_<:< c.universe.typeOf[Short]

  /**
    * Generate reader for specified type
    *
    * @param c   Macro context
    * @param src Tree representing a `ByteIterator`
    * @return Reader tree
    */
  override def generateReader(c: blackbox.Context)(src: c.Tree): c.Tree = {
    import c.universe._

    q"$src.getShort(_root_.java.nio.ByteOrder.BIG_ENDIAN)"
  }

  /**
    * Generate writer for specified type
    *
    * @param c    Macro context
    * @param data Tree representing an input data
    * @param dst  Tree representing a `ByteStringBuilder`
    * @return Writer tree
    */
  override def generateWriter(c: blackbox.Context)(data: c.Tree, dst: c.Tree): c.Tree = {
    import c.universe._

    q"$dst.putShort($data)(_root_.java.nio.ByteOrder.BIG_ENDIAN)"
  }
}
