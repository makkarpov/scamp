package ru.makkarpov.scamp

import ru.makkarpov.scamp.macroz.scalarType
import ru.makkarpov.scamp.macroz.types._

object Types {
  @scalarType[ByteTypeHandler]
  def byte = ProtocolDef

  @scalarType[DoubleTypeHandler]
  def double = ProtocolDef

  @scalarType[FloatTypeHandler]
  def float = ProtocolDef

  @scalarType[IntTypeHandler]
  def int = ProtocolDef

  @scalarType[LongTypeHandler]
  def long = ProtocolDef

  @scalarType[RawBytesHandler]
  def rawBytes = ProtocolDef

  @scalarType[ShortTypeHandler]
  def short = ProtocolDef

  @scalarType[StringTypeHandler]
  def string = ProtocolDef

  @scalarType[UnsignedByteTypeHandler]
  def unsignedByte = ProtocolDef

  @scalarType[UnsignedShortTypeHandler]
  def unsignedShort = ProtocolDef

  @scalarType[VarIntTypeHandler]
  def varInt = ProtocolDef

  @scalarType[VarLongTypeHandler]
  def varLong = ProtocolDef
}
