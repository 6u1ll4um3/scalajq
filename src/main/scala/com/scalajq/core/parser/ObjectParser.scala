package com.scalajq.core.parser

import com.scalajq.core.Operator._
import com.scalajq.core.parser.FilterParser.filters
import com.scalajq.core.Pair
import fastparse.NoWhitespace._
import fastparse._


object ObjectParser {

  private def name[Any: P]: P[String] = P(space.? ~ (CharIn("a-z", "A-Z", "_") ~ CharIn("a-z", "A-Z", "0-9", "_").rep).!)

  private def pair[_: P]: P[Pair] = P(name ~ ":" ~ filters).map(t => Pair(t._1,t._2))

  def obj[_: P]: P[Seq[Pair]] = P(Start ~ "{" ~ pair.rep(sep=",") ~ "}" ~ End)

}
