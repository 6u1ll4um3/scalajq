package com.scalajq.core

import com.scalajq.core.Operator._
import fastparse.NoWhitespace._
import fastparse._

object Parser {

  def trueTerm[_: P]: P[BooleanTerm]  = `true`.map(_ => BooleanTerm(true))

  def falseTerm[_: P]: P[BooleanTerm] = `false`.map(_ => BooleanTerm(false))

  def numberTerm[_: P]: P[NumberTerm] = num.map(x => NumberTerm(x.toDouble))

  def stringTerm[_: P]: P[StringTerm] = string.map(StringTerm)

  def sliceOrIndex[_: P]: P[(Option[Term], Option[NumberTerm], Option[String])] =
    "[" ~ (stringTerm | numberTerm).? ~ ":".? ~ numberTerm.? ~ "]" ~ optional

  def sliceOrIndexModel[_: P]: P[Option[SliceOrIndexModel]] = sliceOrIndex.map {
    case (Some(n1), Some(n2), n3)   => SliceOrIndexModel(n1, Some(n2), n3)
    case (Some(n1), None, n3)       => SliceOrIndexModel(n1, None, n3)
    case _                          => SliceOrIndexModel(NullTerm, None, None)
  }.?

  def fieldWithSlice[_: P]: P[Seq[(Seq[(String, Option[String])], Option[SliceOrIndexModel])]] =
    ((field ~ optional).rep(1) ~ sliceOrIndexModel).rep(1)

  def fieldTerm[_: P]: P[SeqTerm] = fieldWithSlice.map { seqField =>
    SeqTerm(seqField.map(seq =>
      wrapTerm(SeqFieldTerm(seq._1.map(field => FieldTerm(field._1, field._2))), seq._2)
    ))
  }

  def sliceOrIndexTerm[_: P]: P[Term] = P(Operator.dot ~ sliceOrIndexModel).map(m => wrapTerm(IdentityTerm, m))

  def wrapTerm(term: Term, model: Option[Model]): Term = model match {
    case None => term
    case Some(SliceOrIndexModel(start, Some(end), optional)) => SliceTerm(term, start, end, optional)
    case Some(SliceOrIndexModel(start, None, optional)) => IndexTerm(term, start, optional)
  }

  def terms[_: P]: P[Seq[Seq[Term]]] = ((fieldTerm | sliceOrIndexTerm) ~ " ".rep).rep(sep=",").rep(sep = "|")

  def exp: P[_] => P[CombineTerm] = terms(_: P[_]).map(t => CombineTerm(t.map(SeqTerm)))

}