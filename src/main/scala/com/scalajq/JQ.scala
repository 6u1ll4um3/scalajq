package com.scalajq

import com.scalajq.core.parser.ObjectParser.obj
import com.scalajq.core.parser.FilterParser.filters
import com.scalajq.core.{Object, Output, Translator}
import fastparse.{P, parse}
import play.api.libs.json.JsValue

object JQ {

  private def outputParser[_: P]: P[Output] = P(obj.map(Object) | filters)
  private def parser: P[_] => P[Output] = outputParser(_: P[_])

  def apply(js: JsValue, jq: String): JsValue = {

    val terms: Output = parse(jq, parser).get.value
    Translator.run(terms, js)
  }
}
