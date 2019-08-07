package com.scalajq.core

sealed trait Term
case object NullTerm extends Term
case object IdentityTerm extends Term
case class SeqTerm(terms: Seq[Term]) extends Term
case class FieldTerm(name: String) extends Term
case class SeqFieldTerm(fields: Seq[FieldTerm]) extends Term
case class SliceTerm(term: Term, start: Term, end: Term) extends Term
case class StringTerm(value: String) extends Term
case class NumberTerm(value: Double) extends Term
case class BooleanTerm(value: Boolean) extends Term
case class IndexTerm(term: Term, exp: Term) extends Term

sealed trait Model
case class SliceOrIndexModel(start: Term, end: Option[Term]) extends Model

