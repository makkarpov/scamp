package ru.makkarpov.scamp

import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.language.implicitConversions

object ProtocolDef extends ProtocolDef[Nothing] {
  @compileTimeOnly("can be used only in protocol() block")
  def packet[T](children: ProtocolDef[T]*): ProtocolDef[T] = ProtocolDef

  @compileTimeOnly("can be used only in protocol() block")
  def limited(limit: Int, field: ProtocolDef[Nothing]): ProtocolDef[Nothing] = ProtocolDef

  // not implemented
  @compileTimeOnly("can be used only in protocol() block")
  def compositeField[T](children: ProtocolDef[T]*): ProtocolDef[Nothing] = ProtocolDef

  @compileTimeOnly("can be used only in protocol() block")
  def selector[T](discriminator: ProtocolDef[Nothing])(children: (Any, ProtocolDef[T])*): ProtocolDef[T] = ProtocolDef

  // not implemented
  @compileTimeOnly("can be used only in protocol() block")
  def selectorField[T](discriminator: ProtocolDef[Nothing])(children: (Any, ProtocolDef[T])*) = ProtocolDef

  @compileTimeOnly("can be used only in protocol() block")
  def defaultNamed(s: String): Unit = ()

  @compileTimeOnly("can be used only in protocol() block")
  implicit def namedDef(inner: (String, ProtocolDef[Nothing])): ProtocolDef[Nothing] = ProtocolDef
}

sealed trait ProtocolDef[+T]
