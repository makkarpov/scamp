package ru.makkarpov.scamp

import ru.makkarpov.scamp.macroz.scalarType
import ru.makkarpov.scamp.macroz.types.{LongTypeHandler, RawBytesHandler, StringTypeHandler}

object Types {
  @scalarType[LongTypeHandler]
  def long = ProtocolDef

  @scalarType[StringTypeHandler]
  def string = ProtocolDef

  @scalarType[RawBytesHandler]
  def rawBytes = ProtocolDef
}
