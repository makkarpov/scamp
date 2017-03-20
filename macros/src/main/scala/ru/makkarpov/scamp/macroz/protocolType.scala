package ru.makkarpov.scamp.macroz

import scala.annotation.StaticAnnotation

case class protocolType[Prov <: TypeProvider]() extends StaticAnnotation
