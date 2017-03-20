package ru.makkarpov.scamp.macroz

import ru.makkarpov.scamp.GenerateProtocol

import scala.reflect.macros.blackbox

trait Utils { this: GenerateProtocol =>
  import c.universe._

  val ownPkg = q"_root_.ru.makkarpov.scamp"
  val predefPkg = q"_root_.scala.Predef"

  case class SelectorStruct(discriminator: FieldStruct, options: Seq[(Tree, Tree)])

  case class FieldStruct(name: Option[String], provider: TypeProvider) {
    private def cast[A, B](x: A): B = x.asInstanceOf[B]

    def appliesTo(t: Type): Boolean = provider.appliesTo(cast(t))
    def makeReader(src: Tree): Tree = cast(provider.makeReader(cast(src)))
    def makeWriter(dst: Tree, data: Tree): Tree = cast(provider.makeWriter(cast(dst), cast(data)))
  }

  object protocolDef {
    // tree -> name, type arg, args
    def unapply(x: Tree): Option[(String, Type, Seq[Tree])] = x match {
      case q"ProtocolDef.$name[$tpe](...$args)" => Some((name.decodedName.toString, tpe.tpe, args.flatten))
      case q"ProtocolDef.$name(...$args)" => Some((name.decodedName.toString, typeOf[Nothing], args.flatten))
      case q"$prefix.ProtocolDef.$name[$tpe](...$args)" => Some((name.decodedName.toString, tpe.tpe, args.flatten))
      case q"$prefix.ProtocolDef.$name(...$args)" => Some((name.decodedName.toString, typeOf[Nothing], args.flatten))
      case _ => None
    }
  }

  object scalarTypeDef {
    // tree -> field name, type handler
    def unapply(x: Tree): Option[FieldStruct] = x match {
      case protocolDef("namedDef", _, Seq(tuple2(nameTree, defTree))) =>
        nameTree match {
          case q"${name: String}" => unapply(defTree).map(_.copy(name = Some(name)))
          case _ => abort("field name should be a string literal, found instead: " + showCode(nameTree))
        }

      case q"$base.$arg(...$args)" =>
        base.symbol.asModule.moduleClass.asClass.toType.declaration(arg).annotations
          .map(_.tpe)
          .filter(_ <:< weakTypeOf[protocolType[_]])
          .collect {
            case TypeRef(_, _, typeArgs) =>
              val provider = Class.forName(typeArgs.head.typeSymbol.fullName)
                .asSubclass(classOf[TypeProvider])
                .getConstructor(classOf[blackbox.Context], classOf[Seq[Tree]])
                .newInstance(c, args.flatten)
              FieldStruct(None, provider)
          }
          .headOption

      case _ => None
    }
  }

  object selectorDef {
    def unapply(x: Tree): Option[SelectorStruct] = x match {
      case protocolDef("selector", baseType, Seq(discTree, choices @ _*)) =>
        val discField = discTree match {
          case scalarTypeDef(data) => data
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
            if (!discField.appliesTo(tpe))
              abort(s"discriminator [${discField.provider}] could not be applied to value of type [$tpe]")
        }

        Some(SelectorStruct(discField, tupleChoices))
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
