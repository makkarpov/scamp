package ru.makkarpov.scamp.macroz.types

import akka.util.ByteString
import ru.makkarpov.scamp.macroz.ScalarTypeHandler

import scala.reflect.macros.blackbox

class RawBytesHandler extends ScalarTypeHandler {
  override def appliesTo(c: blackbox.Context)(tpe: c.Type): Boolean = tpe <:< c.universe.typeOf[ByteString]

  override def generateReader(c: blackbox.Context)(src: c.Tree): c.Tree = {
    import c.universe._

    q"_root_.ru.makkarpov.scamp.CommonFormats.readRawBytes($src)"
  }

  override def generateWriter(c: blackbox.Context)(data: c.Tree, dst: c.Tree): c.Tree = {
    import c.universe._

    q"_root_.ru.makkarpov.scamp.CommonFormats.writeRawBytes($data, $dst)"
  }

}
