package ru.makkarpov.scamp.macroz

import scala.reflect.macros.blackbox

abstract class TypeProvider(val c: blackbox.Context)(rawArgs: Seq[blackbox.Context#Tree]) {
  import c.universe._

  protected val args = rawArgs.asInstanceOf[Seq[Tree]]
  protected def abort(s: String): Nothing = c.abort(c.enclosingPosition, s)

  def appliesTo(tpe: Type): Boolean

  /**
    * @param src Tree representing a `ByteIterator`
    */
  def makeReader(src: Tree): Tree

  /**
    * @param dst Tree representing a `ByteStringBuilder`
    * @param data Tree representing the data being written
    */
  def makeWriter(dst: Tree, data: Tree): Tree

  override def toString: String = getClass.getSimpleName
}
