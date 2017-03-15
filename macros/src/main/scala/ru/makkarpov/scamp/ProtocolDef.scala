package ru.makkarpov.scamp

import scala.language.experimental.macros
import scala.language.implicitConversions

object ProtocolDef extends ProtocolDef[Nothing] {
  def packet[T](children: ProtocolDef[T]*): ProtocolDef[T] = ProtocolDef

  def compositeField[T](children: ProtocolDef[T]*): ProtocolDef[Nothing] = ProtocolDef

  def selector[T](discriminator: ProtocolDef[Nothing])(children: (Any, ProtocolDef[T])*): ProtocolDef[T] = ProtocolDef
  def selectorField[T](discriminator: ProtocolDef[Nothing])(children: (Any, ProtocolDef[T])*) = ProtocolDef

  def defaultNamed(s: String): Unit = ()

  implicit def namedDef(inner: (String, ProtocolDef[Nothing])): ProtocolDef[Nothing] = ProtocolDef
}

sealed trait ProtocolDef[+T]
