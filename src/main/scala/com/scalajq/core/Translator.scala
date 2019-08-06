package com.scalajq.core

import play.api.libs.json.{JsArray, JsNull, JsNumber, JsString, JsValue, Json}

case class JqFunction(fn: JsValue => JsValue) {
  def apply(in: JsValue): JsValue = fn(in)
}

object Translator {

  val identityToFunction = JqFunction(input => input )

  def run(exp: Exp, input: JsValue): JsValue = {
    exp match {
      case TermsExp(ts) if ts.tail.nonEmpty     => termsToFunction(ts, input)
      case TermsExp(ts) if ts.tail.isEmpty      => termToFunction(ts.head)(input)
      case TermExp(t)                           => termToFunction(t)(input)
      case e                                    => throw new Exception(s"run, unable to handle expression $e")
    }
  }

  def termsToFunction(terms: Seq[Term], input: JsValue): JsArray = {

    val headTerm: JsValue = termToFunction(terms.head)(input)
    if (terms.tail.isEmpty) {
      Json.arr(headTerm)
    } else {
      headTerm +: termsToFunction(terms.tail, input)
    }
  }

  def termToFunction(term: Term): JqFunction = {
    term match {
      case IdentityTerm               => identityToFunction
      case FieldTerm(fields)          => composeList(fields.map {
                                        case (FieldModel(name), _)    => fieldToFunction(JsString(name))
                                      })
      case SeqTerm(terms)             => seqToFunction(terms)
      case IndexTerm(t, idx)          => indexToFunction(t, idx)
      case SliceTerm(t, start, end)   => sliceToFunction(t, start, end)
      case StringTerm(str)            => constantToFunction(JsString(str))
      //case NumberTerm(n)              => constantToFunction(JsNumber(n.value))
      case NumberTerm(n)              => constantToFunction(JsNumber(n))
      case NullTerm                   => constantToFunction(JsNull)
      case t                          => throw new Exception(s"termToFunction, term type not manage : $t")
    }
  }

  def seqToFunction(terms: Seq[Term]): JqFunction = JqFunction { input =>
    if(terms.tail.isEmpty) {
      termToFunction(terms.head)(input)
    } else {
      seqToFunction(terms.tail)(termToFunction(terms.head)(input))
    }
  }

  def expToFunction(exp: Exp): JqFunction = exp match {
    case TermExp(term)     => termToFunction(term)
    case e                 => throw new Exception(s"expToFunction, unable to handle expression $e")
  }

  def indexToFunction(term: Term, index: Exp) = JqFunction { input =>

    val termFunction = termToFunction(term)
    val indexFunction = expToFunction(index)
    val trm = termFunction(input)
    val idx = indexFunction(trm)

    fieldToFunction(idx)(trm)
  }

  def fieldToFunction(field: JsValue): JqFunction = JqFunction { input =>

    field match {
      case JsNumber(n) =>
        input match {
          case JsArray(value) if n >= 0 && n < value.length => value(n.toInt)
          case JsArray(value) if n<0 =>
            val reverseIndex = value.length + n.toInt
            if (reverseIndex >= 0) {
              value(reverseIndex)
            } else {
              JsNull
            }
          case e => throw new Exception(s"fieldToFunction, input $e not supported, $field")
        }
      case JsString(str) => (input \ str).toOption.getOrElse(throw new Exception(s"fieldToFunction, unable to get field"))
      case e  => throw new Exception(s"fieldToFunction, field not supported $e")
    }
  }

  def sliceToFunction(trm: Term, startExp: Exp, endExp: Exp) = JqFunction { input =>

    val termFunction = termToFunction(trm)
    val startFunction = expToFunction(startExp)
    val endFunction = expToFunction(endExp)
    val term: JsValue = termFunction(input)

    val startIdx = startFunction(term).asOpt[Int].getOrElse(throw new Exception("sliceToFunction, Int expected"))
    val endIdx = endFunction(term).asOpt[Int].getOrElse(throw new Exception("sliceToFunction, Int expected"))

    term match {
      case _:JsArray => {
        val indices = startIdx until endIdx
        val functions: Seq[JqFunction] = indices.map(idx => indexToFunction(trm, TermExp(NumberTerm(idx))))
        JsArray(functions.map(f => f(input)))
      }
      case JsString(str) => JsString(str.substring(startIdx, endIdx))
      case e => throw new Exception(s"sliceToFunction, input $e not supported")
    }
  }

  def constantToFunction(value: JsValue) = JqFunction { _ => value }

  def composeList(lst: List[JqFunction]): JqFunction = lst.foldLeft(identityToFunction) {
    (acc, next) => JqFunction { input =>
      val previousResult = acc(input)
      next(previousResult)
    }
  }
}
