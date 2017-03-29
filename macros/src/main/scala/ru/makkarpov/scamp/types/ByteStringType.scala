package ru.makkarpov.scamp.types

import akka.util.ByteString
import ru.makkarpov.scamp.macroz.TypeProvider

import scala.reflect.macros.blackbox

class ByteStringType(x: blackbox.Context)(rawArgs: Seq[blackbox.Context#Tree]) extends TypeProvider(x)(rawArgs) {
  import c.universe._

  val maxLength: Tree = args.head
  val commonFormats = q"_root_.ru.makkarpov.scamp.CommonFormats"

  override def appliesTo(tpe: Type): Boolean = tpe =:= typeOf[ByteString]

  override def makeReader(src: Tree): Tree = q"$commonFormats.readByteString($src, $maxLength)"

  override def makeWriter(dst: Tree, data: Tree): Tree = q"$commonFormats.writeByteString($dst, $data, $maxLength)"
}
