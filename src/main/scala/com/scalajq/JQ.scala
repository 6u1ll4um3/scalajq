package com.scalajq

import com.scalajq.core.{Parser, SeqTerm, Translator}
import fastparse.parse
import play.api.libs.json.JsValue

object JQ {

  def apply(js: JsValue, q: String): JsValue = {

    val terms: SeqTerm = parse(q, Parser.exp).get.value
    Translator.run(terms, js)
  }
}
