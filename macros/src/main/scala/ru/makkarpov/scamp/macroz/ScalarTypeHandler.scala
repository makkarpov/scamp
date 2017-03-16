package ru.makkarpov.scamp.macroz

import scala.annotation.StaticAnnotation
import scala.reflect.macros.blackbox

/**
  * @tparam H Scalar type handler
  */
case class scalarType[H <: ScalarTypeHandler]() extends StaticAnnotation

/**
  * Handler for scalar type (any type that isn't a container nor selector)
  */
trait ScalarTypeHandler {
  def appliesTo(c: blackbox.Context)(tpe: c.Type): Boolean

  /**
    * Generate reader for specified type
    * @param c Macro context
    * @param src Tree representing a `ByteIterator`
    * @return Reader tree
    */
  def generateReader(c: blackbox.Context)(src: c.Tree): c.Tree

  def generateLimitedReader(c: blackbox.Context)(src: c.Tree, limit: Int): c.Tree =
    generateReader(c)(src)

  /**
    * Generate writer for specified type
    * @param c Macro context
    * @param data Tree representing an input data
    * @param dst Tree representing a `ByteStringBuilder`
    * @return Writer tree
    */
  def generateWriter(c: blackbox.Context)(data: c.Tree, dst: c.Tree): c.Tree
}
