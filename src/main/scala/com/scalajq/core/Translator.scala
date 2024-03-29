package com.scalajq.core

import play.api.libs.json.{JsArray, JsNull, JsNumber, JsObject, JsString, JsValue, Json}

case class JqFunction(fn: JsValue => JsValue) {
  def apply(in: JsValue): JsValue = fn(in)
}

object Translator {

  val identityToFunction = JqFunction(input => input )

  def run(output: Output, input: JsValue): JsValue = {
    output match {
      case o:Object => buildObject(o, input: JsValue)
      case f:Filter => buildFilter(f, input: JsValue)
      case a:Array  => buildArray(a, input: JsValue)
    }
  }

  def buildArray(arr: Array, input: JsValue): JsValue = buildFilter(arr.filters, input)

  def buildObject(obj: Object, input: JsValue): JsValue = {

    val jsElem: Seq[JsObject] = obj.nodes.map { node =>

      val jsName = node.name match {
        case f:Filter   => buildFilter(f, input).as[JsString].value
        case str:String => str
        case e          => throw new Exception(s"buildObject, unable to handle expression $e")
      }

      node.value match {
        case f:Filter => Json.obj(jsName -> buildFilter(f, input))
        case a:Array  => Json.obj(jsName -> buildArray(a, input))
        case o:Object => Json.obj(jsName -> buildObject(o, input))
      }
    }
    jsElem.foldLeft(Json.obj())((a,b) => a ++ b)
  }

  def buildFilter(filters: Filter, input: JsValue): JsValue = {
    filters match {
      case Filter(ts) if ts.tail.nonEmpty  => buildFilter(Filter(filters.terms.tail), buildTerms(filters.terms.head, input))
      case Filter(ts) if ts.tail.isEmpty   => buildTerms(filters.terms.head, input)
      case e                               => throw new Exception(s"buildFilter, unable to handle expression $e")
    }
  }

  def buildTerms(seq: SeqTerm, input: JsValue): JsValue = {
    seq match {
      case SeqTerm(ts) if ts.tail.nonEmpty     => termsToJsArray(ts, input)
      case SeqTerm(ts) if ts.tail.isEmpty      => termToFunction(ts.head)(input)
      case e                                   => throw new Exception(s"buildTerms, unable to handle expression $e")
    }
  }

  def termsToJsArray(terms: Seq[Term], input: JsValue): JsArray = {

    val headTerm: JsValue = termToFunction(terms.head)(input)
    if (terms.tail.isEmpty) {
      Json.arr(headTerm)
    } else {
      headTerm +: termsToJsArray(terms.tail, input)
    }
  }

  def termToFunction(term: Term): JqFunction = {

    term match {
      case IdentityTerm                     => identityToFunction
      case SeqFieldTerm(fields)             => composeList(fields.map(f => fieldToFunction(JsString(f.name), f.optional)))
      case SeqTerm(terms)                   => seqToFunction(terms)
      case IndexTerm(t, idx, opt)           => indexToFunction(t, idx, opt)
      case SliceTerm(t, start, end, opt)    => sliceToFunction(t, start, end, opt)
      case StringTerm(str)                  => constantToFunction(JsString(str))
      case NumberTerm(n)                    => constantToFunction(JsNumber(n))
      case NullTerm                         => constantToFunction(JsNull)
      case RecTerm                          => recursiveToFunction()
      case t                                => throw new Exception(s"termToFunction, term type not manage : $t")
    }
  }

  def recursiveToFunction(): JqFunction = JqFunction {
    case JsArray(value) => recursiveToFunction()(value.head)
    case value          => value
  }

  def seqToFunction(terms: Seq[Term]): JqFunction = JqFunction { input =>
    if(terms.tail.isEmpty) {
      termToFunction(terms.head)(input)
    } else {
      seqToFunction(terms.tail)(termToFunction(terms.head)(input))
    }
  }

  def indexToFunction(term: Term, index: Term, opt: Option[String]) = JqFunction { input =>

    val termFunction = termToFunction(term)
    val indexFunction = termToFunction(index)
    val trm = termFunction(input)
    val idx = indexFunction(trm)

    fieldToFunction(idx, opt)(trm)
  }

  def optionalResult(optional: Option[String], field: JsValue, input: JsValue): JsValue = {
    optional match {
      case Some("?")  => JsNull
      case _          => throw new IllegalArgumentException(s"input $input not supported, $field")
    }
  }

  def fieldToFunction(field: JsValue, optional: Option[String]): JqFunction = JqFunction { input =>

    field match {
      case JsNumber(n) =>
        input match {
          case JsArray(value) if n >= 0 && n < value.length => value(n.toInt)
          case JsArray(value) if n < 0 =>
            val reverseIndex = value.length + n.toInt
            if (reverseIndex >= 0) {
              value(reverseIndex)
            } else {
              JsNull
            }
          case _ => optionalResult(optional, field, input)
        }
      case JsString(str) => (input \ str).toOption match {
        case Some(s) => s
        case _ =>
          input  match {
            case JsArray(value) =>
              JsArray(value.map(node => (node \ str).toOption.getOrElse(optionalResult(optional, field, input))))
            case _ => optionalResult(optional, field, input)
          }
      }
      case JsNull => input
      case e      => throw new Exception(s"fieldToFunction, field $e not supported")
    }
  }

  def sliceToFunction(trm: Term, startExp: Term, endExp: Term, opt: Option[String]) = JqFunction { input =>

    val termFunction = termToFunction(trm)
    val startFunction = termToFunction(startExp)
    val endFunction = termToFunction(endExp)
    val term: JsValue = termFunction(input)

    val startIdx = startFunction(term).asOpt[Int].getOrElse(throw new Exception("sliceToFunction, Int expected"))
    val endIdx = endFunction(term).asOpt[Int].getOrElse(throw new Exception("sliceToFunction, Int expected"))

    term match {
      case _:JsArray =>
        val indices = startIdx until endIdx
        val functions: Seq[JqFunction] = indices.map(idx => indexToFunction(trm, NumberTerm(idx), opt))
        JsArray(functions.map(f => f(input)))

      case JsString(str) => JsString(str.substring(startIdx, endIdx))
      case e => throw new Exception(s"sliceToFunction, input $e not supported")
    }
  }

  def constantToFunction(value: JsValue) = JqFunction { _ => value }

  def composeList(lst: Seq[JqFunction]): JqFunction = lst.foldLeft(identityToFunction) {
    (acc, next) => JqFunction { input =>
      val previousResult = acc(input)
      next(previousResult)
    }
  }
}
