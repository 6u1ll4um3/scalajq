package com.scalajq

import com.scalajq.core.parser.ObjectParser.obj
import com.scalajq.core.parser.FilterParser.filters
import com.scalajq.core.{Filters, Object, Output, SeqTerm, Translator}
import fastparse.{P, parse}
import play.api.libs.json.JsValue

object JQ {

  //private def exprFilter[_: P]: P[Filters] = filters.map(t => Filters(t.map(SeqTerm)))
  private def exprObject[_: P]: P[Object] = obj.map(Object)
  private def exprOutput[_: P]: P[Output] = P(exprObject | filters)
  private def expr: P[_] => P[Output] = exprOutput(_: P[_])

  def apply(js: JsValue, jq: String): JsValue = {

    val terms: Output = parse(jq, expr).get.value
    Translator.run(terms, js)
  }
}
