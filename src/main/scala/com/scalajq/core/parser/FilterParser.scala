package com.scalajq.core.parser

import com.scalajq.core.Operator._
import com.scalajq.core._
import fastparse.NoWhitespace._
import fastparse._

object FilterParser {

  private def recursiveTerm[_: P]: P[RecTerm.type] = rec.map(_ => RecTerm)

  private def numberTerm[_: P]: P[NumberTerm] = num.map(x => NumberTerm(x.toDouble))

  private def stringTerm[_: P]: P[StringTerm] = string.map(StringTerm)

  private def sliceOrIndex[_: P]: P[(Option[Term], Option[NumberTerm], Option[String])] =
    "[" ~ (stringTerm | numberTerm).? ~ ":".? ~ numberTerm.? ~ "]" ~ optional

  private def sliceOrIndexModel[_: P]: P[Option[SliceOrIndexModel]] = sliceOrIndex.map {
    case (Some(n1), Some(n2), n3)   => SliceOrIndexModel(n1, Some(n2), n3)
    case (Some(n1), None, n3)       => SliceOrIndexModel(n1, None, n3)
    case _                          => SliceOrIndexModel(NullTerm, None, None)
  }.?

  private def fieldWithSlice[_: P]: P[Seq[(Seq[(String, Option[String])], Option[SliceOrIndexModel])]] =
    ((field ~ optional).rep(1) ~ sliceOrIndexModel).rep(1)

  private def fieldTerm[_: P]: P[SeqTerm] = fieldWithSlice.map { seqField =>
    SeqTerm(seqField.map(seq =>
      wrapTerm(SeqFieldTerm(seq._1.map(field => FieldTerm(field._1, field._2))), seq._2)
    ))
  }

  private def sliceOrIndexTerm[_: P]: P[Term] = P(Operator.dot ~ sliceOrIndexModel).map(m => wrapTerm(IdentityTerm, m))

  private def wrapTerm(term: Term, model: Option[Model]): Term = model match {
    case None => term
    case Some(SliceOrIndexModel(start, Some(end), optional)) => SliceTerm(term, start, end, optional)
    case Some(SliceOrIndexModel(start, None, optional)) => IndexTerm(term, start, optional)
  }

  private def seqTerm[_: P]: P[Seq[Seq[Term]]] = (
    (
      recursiveTerm |
      fieldTerm |
      sliceOrIndexTerm
    ) ~ " ".rep).rep(sep=",").rep(sep = "|")

  def filters[_: P]: P[Filters] = seqTerm.map(t => Filters(t.map(SeqTerm)))

}
