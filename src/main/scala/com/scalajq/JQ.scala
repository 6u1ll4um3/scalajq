package com.scalajq

import argonaut.{Json => ArgJson}
import com.scalajq.core.{Exp, Translator, Parser}
import fastparse.parse
import scalaz.Isomorphism.<=>

object JQ {

  def apply[JS](js: JS, q: String)(implicit iso: JS <=> ArgJson): JS = {

    val exp: Exp = parse(q, Parser.exp).get.value
    val argJson: ArgJson = Translator.run(exp, iso.to(js))
    iso.from(argJson)
  }
}
