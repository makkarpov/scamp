package ru.makkarpov.scamp

import ru.makkarpov.scamp.macroz.{Compat, SerializerGenerator, Utils}

import scala.collection.mutable
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object GenerateProtocol {
  def protocol[T](children: (Int, ProtocolDef[T])*): PacketSerializer[T] = macro macroImpl[T]
  def macroImpl[T: c.WeakTypeTag](c: blackbox.Context)(children: c.Tree*): c.Tree = {
    val impl = new GenerateProtocol(c)
    val tpe = implicitly[c.WeakTypeTag[T]].tpe.asInstanceOf[impl.c.Type]
    impl.generate(tpe, children.asInstanceOf[Seq[impl.c.Tree]]).asInstanceOf[c.Tree]
  }
}

class GenerateProtocol(val c: blackbox.Context) extends SerializerGenerator with Compat with Utils {
  import c.universe._

  def abort(s: String): Nothing = c.abort(c.enclosingPosition, s)

  case class RootSerializerResult(serializer: Tree, packetTypes: Set[Type])

  def generate(packetType: Type, specs: Seq[Tree]): Tree = {
    val subTrees = specs.map {
      case tuple2(q"${id: Int}", spec) => spec match {
        case packetDef(tpe, args) =>
          if (tpe =:= typeOf[Nothing])
            abort(s"packet type should be specified (inferred Nothing at packet with ID #$id)")
          if (!(tpe <:< packetType))
            abort(s"packet type is not a subtype of protocol packet type (at packet with ID #$id)")
          (id, generateSerializer(tpe, args))
        case _ => abort("`protocol(...)` definition should contain `packet(...)` definitions at outer level")
      }
      case x => abort("expected (id, spec) tuple definition, found instead:" + showCode(x))
    }

    val indices = subTrees.indices
    val fieldNames = specs.map(_ => TermName(c.freshName("serializer")))

    def fieldAssignments = indices.map(i => q"val ${fieldNames(i)} = ${subTrees(i)._2.serializer}")
    def readEntries = indices.map(i => q"(${subTrees(i)._1}, ${fieldNames(i)})")
    def writeEntries = indices.flatMap { i =>
      subTrees(i)._2.packetTypes.map(t => q"($predefPkg.classOf[$t], ${fieldNames(i)})")
    }

    val ret = q"""
      new $ownPkg.PacketSerializer.Default[$packetType] {
        ..$fieldAssignments

        val packetIds = $predefPkg.Map[$predefPkg.Int, $ownPkg.InternalSerializer[_ <: $packetType]](..$readEntries)
        val packetTypes = $predefPkg.Map[_root_.java.lang.Class[_ <: $packetType], $ownPkg.InternalSerializer[_ <: $packetType]](..$writeEntries)

        def forPacket(p: $packetType): Ser = packetTypes(p.getClass)
        def forId(id: $predefPkg.Int): Ser = packetIds(id)
      }
    """

//    abort(showCode(ret))

    ret
  }
}
