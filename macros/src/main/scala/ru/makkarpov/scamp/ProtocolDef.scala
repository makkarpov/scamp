package ru.makkarpov.scamp

import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.language.implicitConversions

object ProtocolDef extends ProtocolDef[Nothing] {
  def protocol[T](children: (Int, ProtocolDef[T])*): PacketSerializer[T] = macro GenerateProtocol.macroImpl[T]

  @compileTimeOnly("can be used only in protocol() block")
  def packet[T](children: ProtocolDef[T]*): ProtocolDef[T] = ProtocolDef

  @compileTimeOnly("can be used only in protocol() block")
  def selector[T](discriminator: ProtocolDef[Nothing])(children: (Any, ProtocolDef[T])*): ProtocolDef[T] = ProtocolDef

  @compileTimeOnly("can be used only in protocol() block")
  def defaultNamed(s: String): Unit = ()

  @compileTimeOnly("can be used only in protocol() block")
  implicit def namedDef(inner: (String, ProtocolDef[Nothing])): ProtocolDef[Nothing] = ProtocolDef
}

sealed trait ProtocolDef[+T]
