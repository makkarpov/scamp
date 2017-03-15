package ru.makkarpov.scamp.macroz

import ru.makkarpov.scamp.GenerateProtocol

trait Compat { this: GenerateProtocol =>
  import c.universe._

  object tuple2 {
    def unapply(t: c.Tree): Option[(c.Tree, c.Tree)] = t match {
      case q"scala.this.Predef.ArrowAssoc[$aType]($ax).->[$bType]($bx)" => Some((ax, bx))
      case _ => None
    }
  }
}
