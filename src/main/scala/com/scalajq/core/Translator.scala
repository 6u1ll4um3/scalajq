package com.scalajq.core

import argonaut.Argonaut.{jNull, jNumber, jString}
import argonaut.{Json, JsonLong}


case class JqFunction(fn: Json => Json) {
  def apply(in: Json): Json = fn(in)
}

object Translator {

  val identityToFunction = JqFunction(input => input )

  def run(exp: Exp, input: Json): Json = {
    exp match {
      case TermExp (t)  => termToFunction(t)(input)
      case e            => throw new Exception(s"Unable to handle expression $e")
    }
  }

  def termToFunction(term: Term): JqFunction = {
    term match {
      case IdentityTerm               => identityToFunction
      case FieldTerm(fields)          => composeList(fields.map {
                                        case (FieldModel(name), _)    => fieldToFunction(jString(name))
                                      })
      case SeqTerm(terms)             => seqToFunction(terms.head, terms.tail)
      case IndexTerm(t, idx)          => indexToFunction(t, idx)
      case StringTerm(str)            => constantToFunction(jString(str))
      case NumberTerm(n)              => constantToFunction(jNumber(n.value))
      case NullTerm                   => constantToFunction(jNull)
      case t                          => throw new Exception(s"term type not manage : $t")
    }
  }

  def seqToFunction(term: Term, terms: Seq[Term]): JqFunction = JqFunction { input =>
    if(terms.isEmpty) {
      termToFunction(term)(input)
    } else {
      seqToFunction(terms.head, terms.tail)(termToFunction(term)(input))
    }
  }

  def expToFunction(exp: Exp): JqFunction = exp match {
    case TermExp(term) => termToFunction(term)
  }

  def indexToFunction(term: Term, index: Exp) = JqFunction { input =>

    val termFunction = termToFunction(term)
    val indexFunction = expToFunction(index)
    val trm = termFunction(input)
    val idx = indexFunction(trm)

    fieldToFunction(idx)(trm)
  }

  def fieldToFunction(field: Json): JqFunction = JqFunction { input =>

    if (field.isString) {
      input.fieldOr(field.stringOrEmpty, jNull)
    } else if (field.isNumber) {
      val jNum = field.numberOr(JsonLong(0))
      val suppliedIndex = jNum.toInt.getOrElse(throw new Exception(s"index is not a Integer $field"))
      val array = input.arrayOrEmpty

      if (suppliedIndex >= 0 && suppliedIndex < array.length) {
        array(suppliedIndex)
      } else if (suppliedIndex < 0) {
        val reverseIndex = array.length + suppliedIndex
        if (reverseIndex >= 0) {
          array(reverseIndex)
        } else jNull
      } else jNull
    } else {
      throw new Exception(s"index expected to be String or Integer $field")
    }
  }

  def constantToFunction(value: Json) = JqFunction { _ => value }

  def composeList(lst: List[JqFunction]): JqFunction = lst.foldLeft(identityToFunction) {
    (acc, next) => JqFunction { input =>
      val previousResult = acc(input)
      next(previousResult)
    }
  }
}
