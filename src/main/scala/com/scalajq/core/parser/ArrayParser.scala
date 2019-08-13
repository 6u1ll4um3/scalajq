package com.scalajq.core.parser

import com.scalajq.core.Array
import fastparse.NoWhitespace._
import fastparse._


object ArrayParser {

  def parser[_: P]: P[Array] = P("[" ~ FilterParser.parser ~ "]").map(Array)

}
