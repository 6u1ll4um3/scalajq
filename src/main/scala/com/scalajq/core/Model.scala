package com.scalajq.core

sealed trait Exp
case class TermExp(term: Term) extends Exp

sealed trait Term
case object RecTerm extends Term
case object NullTerm extends Term
case object IdentityTerm extends Term
case class SeqTerm(terms: Seq[Term]) extends Term
case class FieldTerm(fields: List[(FieldModel, Boolean)]) extends Term
case class SliceTerm(term: Term, start: Exp, end: Exp) extends Term
case class StringTerm(value: String) extends Term
case class NumberTerm(value: NumberModel) extends Term
case class IndexTerm(term: Term, exp: Exp) extends Term

sealed trait Model
case class FormatModel(name: String) extends Model
case class NumberModel(value: Double) extends Model
case class StringModel(value: String) extends Model
case class BooleanModel(value: Boolean) extends Model
case class IdentifierModel(name: String) extends Model
case class FieldModel(name: String) extends Model

sealed trait Intermediate
case class SliceOrIndexModel(start: Exp, end: Option[Exp]) extends Intermediate

