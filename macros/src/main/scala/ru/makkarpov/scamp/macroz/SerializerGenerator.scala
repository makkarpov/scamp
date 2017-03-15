package ru.makkarpov.scamp.macroz

import ru.makkarpov.scamp.GenerateProtocol

trait SerializerGenerator { this: GenerateProtocol =>
  import c.universe._

  sealed trait FieldLike
  case class Field(name: Option[String], varName: TermName, handler: ScalarTypeHandler) extends FieldLike
  case class Discriminator(value: Tree, handler: ScalarTypeHandler) extends FieldLike

  case class ReadGenerationContext(fields: Seq[Field], postFieldRead: Seq[Tree], packetType: Type)
  case class WriteGenerationContext(preFields: Seq[FieldLike], postFields: Seq[FieldLike], packetType: Type)

  def decodeFields(args: Seq[Tree], needNames: Boolean): (Seq[Field], Option[SelectorStruct], Seq[Field]) =
    args.foldLeft(Seq.empty[Field], Option.empty[SelectorStruct], Seq.empty[Field]) {
      case ((pre, sel, post), x) => x match {
        case scalarTypeDef(name, handler) =>
          val varName = TermName(if (needNames) c.freshName("field") else "")
          val field = Field(name, varName, handler)
          if (sel.isEmpty) (pre :+ field, sel, post)
          else (pre, sel, post :+ field)

        case selectorDef(data) =>
          if (sel.isDefined)
            abort("packet must not contain multiple selectors at same nesting level")
          (pre, Some(data), post)

        case _ => abort(s"failed to match field definition: ${showCode(x)}")
      }
    }

  def matchFields(fields: Seq[Field], target: Type): Seq[Field] = {
    val fieldNames = fields.flatMap(_.name)
    val conflicts = fieldNames.groupBy(identity).collect { case (s, x) if x.size > 1 => s }
    if (conflicts.nonEmpty)
      abort(s"non-unique field names: ${conflicts.mkString(", ")}")

    val classFields = target.typeSymbol.asClass.primaryConstructor.asMethod.paramLists.head
    val namedFields = fields.collect { case x @ Field(Some(name), _, _) => name -> x }.toMap
    val seqFields = fields.collect { case x @ Field(None, _, _) => x }

    val (_, ret) = classFields.foldLeft(0, Seq.empty[Field]) {
      case ((idx, acc), f) =>
        val name = f.name.decodedName.toString
        val (field, newIdx) = namedFields.get(name) match {
          case Some(x) => (x, idx)
          case None if idx < seqFields.size => (seqFields(idx), idx + 1)
          case None =>
            abort(s"not enough sequential fields to fill this class: unmatched name: $name")
        }

        if (!field.handler.appliesTo(c)(f.typeSignature))
          abort(s"field handler [${field.handler}] cannot be applied to [${f.typeSignature}]")

        (newIdx, acc :+ field)
    }

    ret
  }

  def generateSerializer(packetType: Type, defs: Seq[Tree]): RootSerializerResult = {
    val dstVar = TermName("dst")
    val srcVar = TermName("src")
    val dataVar = TermName("data")

    val reads: Tree = {
      def constructPacket(fields: Seq[Field], target: Type): c.Tree = {
        val trees = matchFields(fields, target).map(x => q"${x.varName}")
        q"new $target(..$trees)"
      }

      def decode(ctx: ReadGenerationContext, args: Seq[Tree]): Tree = {
        // There should be only one selector per nesting level. So split it to header, selector and footer
        val (preFields, selector, postFields) = decodeFields(args, needNames = true)
        val preFieldsRead = preFields.map(x => q"val ${x.varName} = ${x.handler.generateReader(c)(q"$srcVar")}")
        val postFieldsRead = ctx.postFieldRead

        selector match {
          case Some(sel) =>
            val discrField = TermName(c.freshName("discriminator"))

            val matches = sel.options.map {
              case (defaultNamedDef(name), packetDef(tpe, subArgs)) =>
                val matchedVar = TermName(c.freshName("matched"))
                val field = Field(Some(name), matchedVar, sel.discriminator)
                val subCtx = ReadGenerationContext((ctx.fields ++ preFields :+ field) ++ postFields, postFieldsRead, tpe)
                cq"$matchedVar => ${decode(subCtx, subArgs)}"

              case (key, packetDef(tpe, subArgs)) =>
                val subCtx = ReadGenerationContext(ctx.fields ++ preFields ++ postFields, postFieldsRead, tpe)
                cq"$key => ${decode(subCtx, subArgs)}"
            }

            q"""
              ..$preFieldsRead
              val $discrField = ${sel.discriminator.generateReader(c)(q"$srcVar")}
              $discrField match { case ..$matches }
            """

          case None =>
            val fields = ctx.fields ++ preFields ++ postFields

            q"""
              ..$preFieldsRead
              ..$postFieldsRead
              ${constructPacket(fields, ctx.packetType)}
            """
        }
      }

      decode(ReadGenerationContext(Nil, Nil, packetType), defs)
    }

    val (writes, classes) = {
      val matchedVar = TermName("matched")

      def encode(ctx: WriteGenerationContext, args: Seq[Tree]): Seq[(Type, Tree)] = {
        val (preFields, selector, postFields) = decodeFields(args, needNames = false)

        selector match {
          case Some(sel) =>
            sel.options.flatMap {
              case (defaultNamedDef(name), packetDef(tpe, subArgs)) =>
                val newField = Field(Some(name), TermName(""), sel.discriminator)
                val subCtx = WriteGenerationContext(
                  preFields = ctx.preFields ++ preFields :+ newField,
                  postFields = postFields ++ ctx.postFields,
                  packetType = tpe
                )

                encode(subCtx, subArgs)

              case (k, packetDef(tpe, subArgs)) =>
                val subCtx = WriteGenerationContext(
                  preFields = ctx.preFields ++ preFields :+ Discriminator(k, sel.discriminator),
                  postFields = postFields ++ ctx.postFields,
                  packetType = tpe
                )

                encode(subCtx, subArgs)
            }

          case None =>
            val fields = ctx.preFields ++ preFields ++ postFields ++ ctx.postFields
            val classFields = ctx.packetType.typeSymbol.asClass.primaryConstructor.asMethod.paramLists.head
            val matchedFields = matchFields(fields.collect { case x: Field => x }, ctx.packetType)

            val fieldExtractors = matchedFields.zipWithIndex.map {
              case (f, i) =>
                val name = classFields(i).name.decodedName.toTermName
                f -> q"$matchedVar.$name"
            }.toMap

            val writeTrees = fields.map {
              case f: Field => f.handler.generateWriter(c)(fieldExtractors(f), q"$dstVar")
              case d: Discriminator => d.handler.generateWriter(c)(d.value, q"$dstVar")
            }

            Seq(ctx.packetType -> cq"$matchedVar: ${ctx.packetType} => ..$writeTrees")
        }
      }

      val matches = encode(WriteGenerationContext(Nil, Nil, packetType), defs)
      (q"$dataVar match { case ..${matches.map(_._2)} }", matches.map(_._1))
    }

    val ret = q"""
      new $ownPkg.InternalSerializer[$packetType] {
        def read($srcVar: _root_.akka.util.ByteIterator): $packetType = $reads
        def write($dataVar: $packetType, $dstVar: _root_.akka.util.ByteStringBuilder): Unit = $writes
      }
    """

    RootSerializerResult(ret, classes.toSet)
  }
}
