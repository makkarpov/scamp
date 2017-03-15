package ru.makkarpov.scamp.macroz

import ru.makkarpov.scamp.GenerateProtocol

trait Utils { this: GenerateProtocol =>
  import c.universe._

  val ownPkg = q"_root_.ru.makkarpov.scamp"
  val predefPkg = q"_root_.scala.Predef"

  case class SelectorStruct(discriminator: ScalarTypeHandler, options: Seq[(Tree, Tree)])

  object protocolDef {
    // tree -> name, type arg, args
    def unapply(x: Tree): Option[(String, Type, Seq[Tree])] = x match {
      case q"ProtocolDef.$name[$tpe](...$args)" => Some((name.decodedName.toString, tpe.tpe, args.flatten))
      case q"ProtocolDef.$name(...$args)" => Some((name.decodedName.toString, typeOf[Nothing], args.flatten))
      case _ => None
    }
  }

  object scalarTypeDef {
    // tree -> field name, type handler
    def unapply(x: Tree): Option[(Option[String], ScalarTypeHandler)] = x match {
      case protocolDef("namedDef", _, Seq(tuple2(nameTree, defTree))) =>
        nameTree match {
          case q"${name: String}" => unapply(defTree).map { case (_, cls) => Some(name) -> cls }
          case _ => abort("field name should be a string literal, found instead: " + showCode(nameTree))
        }

      case q"$base.$arg" =>
        base.symbol.asModule.moduleClass.asClass.toType.declaration(arg).annotations
          .map(_.tpe)
          .filter(_ <:< weakTypeOf[scalarType[_]])
          .collect {
            case TypeRef(_, _, args) =>
              None -> Class.forName(args.head.typeSymbol.fullName).asSubclass(classOf[ScalarTypeHandler]).newInstance()
          }
          .headOption

      case _ => None
    }
  }

  object selectorDef {
    def unapply(x: Tree): Option[SelectorStruct] = x match {
      case protocolDef("selector", baseType, Seq(discTree, choices @ _*)) =>
        val discHandler = discTree match {
          case scalarTypeDef(_, handler) => handler
          case _ => abort("discriminator must be a scalar type")
        }

        val tupleChoices = choices.map {
          case tuple2(k, v) => (k, v)
          case _ => abort("selector choice must be a tuple")
        }

        for ((k, _) <- tupleChoices) k match {
          case defaultNamedDef(_) =>
          case _ =>
            val tpe = c.typecheck(k, silent = true).tpe
            if (!discHandler.appliesTo(c)(tpe))
              abort(s"discriminator [$discHandler] could not be applied to value of type [$tpe]")
        }

        Some(SelectorStruct(discHandler, tupleChoices))
      case _ => None
    }
  }

  // misc

  object packetDef {
    def unapply(x: Tree): Option[(Type, Seq[Tree])] = x match {
      case protocolDef("packet", tpe, args) => Some((tpe, args))
      case _ => None
    }
  }

  object defaultNamedDef {
    def unapply(x: Tree): Option[(String)] = x match {
      case protocolDef("defaultNamed", _, Seq(name)) => name match {
        case q"${s: String}" => Some(s)
        case _ => abort("defaultNamed() should contain a string literal")
      }

      case _ => None
    }
  }
}
