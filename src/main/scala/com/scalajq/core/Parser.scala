package com.scalajq.core

import com.scalajq.core.Operator._
import fastparse.NoWhitespace._
import fastparse._

object Parser {

  def numberTerm[_: P]: P[NumberTerm] = num.map(x => NumberTerm(x.toDouble))

  def stringTerm[_: P]: P[StringTerm] = string.map(StringTerm)

  def trueTerm[_: P]: P[BooleanTerm]  = `true`.map(_ => BooleanTerm(true))

  def falseTerm[_: P]: P[BooleanTerm] = `false`.map(_ => BooleanTerm(false))

  def sliceOrIndexModel[_: P]: P[Option[SliceOrIndexModel]] =
    P("[" ~ (stringTerm | numberTerm) ~ ":".? ~ numberTerm.? ~ "]" ~ optional).map {
      case (n1, Some(n2), n3)   => SliceOrIndexModel(n1, Some(n2), n3)
      case (n1, None, n3)       => SliceOrIndexModel(n1, None, n3)
    }.?

  def fieldTerm[_: P]: P[SeqTerm] = ((field ~ optional).rep(1) ~ sliceOrIndexModel).rep(1).map { seqField =>
    val seqTerm: Seq[Term] = seqField.map { s =>
      wrapTerm(SeqFieldTerm(s._1.map(t => FieldTerm(t._1, t._2))), s._2)
    }

    SeqTerm(seqTerm)
  }

  def sliceOrIndexTerm[_: P]: P[Term] = P(Operator.dot ~ sliceOrIndexModel).map(m => wrapTerm(IdentityTerm, m))

  def wrapTerm(t: Term, idx: Option[Model]): Term = idx match {
    case None => t
    case Some(SliceOrIndexModel(e1, Some(e2), e3)) => SliceTerm(t, e1, e2, e3)
    case Some(SliceOrIndexModel(e, None, e3)) => IndexTerm(t, e, e3)
  }

  def terms[_: P]: P[Seq[Term]] = P(fieldTerm | sliceOrIndexTerm).rep(sep=",")

  def exp: P[_] => P[SeqTerm] = terms(_: P[_]).map(SeqTerm)

}