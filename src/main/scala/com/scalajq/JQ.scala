package com.scalajq

import com.scalajq.core.parser.{ ArrayParser, ObjectParser, FilterParser }
import com.scalajq.core.{Output, Translator}
import fastparse.{P, parse}
import play.api.libs.json.JsValue

object JQ {

  def parser[_: P]: P[Output] = P(ArrayParser.parser | ObjectParser.parser | FilterParser.parser)

  def apply(js: JsValue, jq: String): JsValue = {

    val terms: Output = parse(jq, parser(_: P[_])).get.value
    Translator.run(terms, js)
  }
}
