package com.scalajq.core

import fastparse.NoWhitespace._
import fastparse._

object Parser {

  def stringTerm[_: P]: P[StringTerm] = Operator.string.map(s => StringTerm(s.value))

  def numberTerm[_: P]: P[NumberTerm] = Operator.num.map(NumberTerm)

  def fieldModel[_: P]: P[(FieldModel, Option[String])] = Operator.field ~ "?".!.?

  def fieldTerm[_: P]: P[SeqTerm] = (fieldModel.rep(1) ~ sliceOrIndexModel).rep(1).map { seqField =>

    val seqTerm: Seq[Term] = seqField.map { s =>
      wrapTerm(FieldTerm(s._1.toList.map { case (f, o) => (f, o.isDefined) }), s._2)
    }
    SeqTerm(seqTerm)
  }

  def sliceOrIndexModel[_: P]: P[Option[SliceOrIndexModel]] = P("[" ~ (stringTerm | numberTerm) ~ ":".? ~ numberTerm.? ~ "]").map {
    case (n1, Some(n2))   => SliceOrIndexModel(TermExp(n1), Some(TermExp(n2)))
    case (n1, None) => SliceOrIndexModel(TermExp(n1), None)
  }.?

  def sliceOrIndexTerm[_: P]: P[Term] = P(Operator.dot ~ sliceOrIndexModel).map(m => wrapTerm(IdentityTerm, m))

  def wrapTerm(t: Term, idx: Option[Intermediate]): Term = idx match {
    case None => t
    case Some(SliceOrIndexModel(e1, Some(e2))) => SliceTerm(t, e1, e2)
    case Some(SliceOrIndexModel(e, None)) => IndexTerm(t, e)
  }

  def term[_: P]: P[Term] = fieldTerm | sliceOrIndexTerm

  def exp: P[_] => P[TermExp] = term(_: P[_]).map(TermExp)
}