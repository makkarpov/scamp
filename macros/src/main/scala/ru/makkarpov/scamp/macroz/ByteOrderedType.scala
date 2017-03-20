package ru.makkarpov.scamp.macroz

import scala.reflect.macros.blackbox

abstract class ByteOrderedType(x: blackbox.Context)(rawArgs: Seq[blackbox.Context#Tree]) extends TypeProvider(x)(rawArgs) {
  import c.universe._

  val byteOrder = q"_root_.java.nio.ByteOrder.BIG_ENDIAN"
}
