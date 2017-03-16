package ru.makkarpov.scamp.macroz.types

import akka.util.ByteString
import ru.makkarpov.scamp.macroz.ScalarTypeHandler

import scala.reflect.macros.blackbox

class RawBytesHandler extends ScalarTypeHandler {
  override def appliesTo(c: blackbox.Context)(tpe: c.Type): Boolean = tpe <:< c.universe.typeOf[ByteString]

  override def generateReader(c: blackbox.Context)(src: c.Tree): c.Tree = {
    import c.universe._

    q"$src.toByteString"
  }


  override def generateLimitedReader(c: blackbox.Context)(src: c.Tree, limit: Int): c.Tree = {
    import c.universe._

    val limitMsg = "Received too long ByteString"
    q"if ($src.len <= $limit) $src.toByteString else throw new _root_.java.io.IOException($limitMsg)"
  }

  override def generateWriter(c: blackbox.Context)(data: c.Tree, dst: c.Tree): c.Tree = {
    import c.universe._

    q"$dst ++= $data"
  }

}
