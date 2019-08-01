package com.scalajq

import com.scalajq.core.{Exp, Parser, Translator}
import fastparse.parse
import play.api.libs.json.JsValue

object JQ {

  def apply(js: JsValue, q: String): JsValue = {

    val exp: Exp = parse(q, Parser.exp).get.value
    Translator.run(exp, js)
  }
}
