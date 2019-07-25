package com.scalajq.core

import fastparse.NoWhitespace._
import fastparse._

object Parser {

  def stringTerm[_: P]: P[Term] = Operator.string.map(lit => StringTerm(lit.value))

  def numberTerm[_: P]: P[Term] = Operator.num.map(nl => NumberTerm(nl))

  def strOrNbTerm[_: P]: P[Term] = stringTerm | numberTerm

  def fieldTerm[_: P]: P[Term] = {
    ((Operator.field ~ "?".!.?).rep(1) ~ indexExpr.?).rep(1).map { seqField =>

      val seqTerm: Seq[Term] = seqField.map { s =>
        wrapIndexTerm(FieldTerm(s._1.toList.map { case (f, o) => (f, o.isDefined) }), s._2)
      }

      SeqTerm(seqTerm)
    }
  }

  def indexTerm[_: P]: P[Term] = P(".") ~ indexExpr.?.map { ie => wrapIndexTerm(IdentityTerm, ie) }

  def indexExpr[_: P]: P[IndexExpr] = ("[" ~ strOrNbTerm ~ "]").map(t => IndexExpr(TermExp(t)))

  def wrapIndexTerm(t: Term, idx: Option[IndexExpr]): Term = idx match {
    case None => t
    case Some(IndexExpr(e)) => IndexTerm(t, e)
  }

  def term[_: P]: P[Term] = fieldTerm | indexTerm

  def exp: P[_] => P[TermExp] = term(_: P[_]).map(TermExp)
}