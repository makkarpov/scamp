package ru.makkarpov.scamp

import ru.makkarpov.scamp.macroz.protocolType
import ru.makkarpov.scamp.types._

object Types {
  @protocolType[ByteType]
  def byte = ProtocolDef

  @protocolType[DoubleType]
  def double = ProtocolDef

  @protocolType[EnumerationType]
  def enum(enumInstance: Enumeration) = ProtocolDef

  @protocolType[FloatType]
  def float = ProtocolDef

  @protocolType[IntType]
  def int = ProtocolDef

  @protocolType[LongType]
  def long = ProtocolDef

  @protocolType[ShortType]
  def short = ProtocolDef

  @protocolType[StringType]
  def string(maxLength: Int = Short.MaxValue) = ProtocolDef

  @protocolType[UnsignedByteType]
  def unsignedByte = ProtocolDef

  @protocolType[UnsignedShortType]
  def unsignedShort = ProtocolDef

  @protocolType[VarIntType]
  def varInt = ProtocolDef

  @protocolType[VarLongType]
  def varLong = ProtocolDef
}
