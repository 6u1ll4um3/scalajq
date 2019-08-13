package com.scalajq.core.parser

import com.scalajq.core.Operator._
import com.scalajq.core.{Object, Pair}
import fastparse.NoWhitespace._
import fastparse._


object ObjectParser {

  private def name[Any: P]: P[String] = P(space.? ~ (CharIn("a-z", "A-Z", "_") ~ CharIn("a-z", "A-Z", "0-9", "_").rep).!)

  private def pair[_: P]: P[Pair] = P(name ~ space.? ~ ":" ~ FilterParser.parser).map(t => Pair(t._1,t._2))

  def parser[_: P]: P[Object] = P("{" ~ pair.rep(sep=",") ~ "}").map(Object)

}
