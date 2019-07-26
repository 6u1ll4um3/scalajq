package com.scalajq.core

import fastparse.NoWhitespace._
import fastparse._

object Parser {

  def stringTerm[_: P]: P[Term] = Operator.string.map(lit => StringTerm(lit.value))

  def numberTerm[_: P]: P[Term] = Operator.num.map(nl => NumberTerm(nl))

  def idxTerm[_: P]: P[Term] = "[" ~ (stringTerm | numberTerm) ~ "]"

  def fieldModel[_: P]: P[(FieldModel, Option[String])] = Operator.field ~ "?".!.?

  def fieldTerm[_: P]: P[Term] = (fieldModel.rep(1) ~ indexModel).rep(1).map { seqField =>

    val seqTerm: Seq[Term] = seqField.map { s =>
      wrapIndexTerm(FieldTerm(s._1.toList.map { case (f, o) => (f, o.isDefined) }), s._2)
    }
    SeqTerm(seqTerm)
  }

  def indexTerm[_: P]: P[Term] = Operator.dot ~ indexModel.map { ie => wrapIndexTerm(IdentityTerm, ie) }

  def indexModel[_: P]: P[Option[IndexModel]] = idxTerm.map(t => IndexModel(TermExp(t))).?

  def wrapIndexTerm(t: Term, idx: Option[IndexModel]): Term = idx match {
    case None => t
    case Some(IndexModel(e)) => IndexTerm(t, e)
  }

  def term[_: P]: P[Term] = fieldTerm | indexTerm

  def exp: P[_] => P[TermExp] = term(_: P[_]).map(TermExp)
}