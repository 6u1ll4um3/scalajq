package com.scalajq

import com.scalajq.core._
import com.scalajq.core.parser.ArrayParser
import fastparse.{P, Parsed, parse}
import org.scalatest.{FlatSpec, MustMatchers}
import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer


class ArrayTest extends FlatSpec with MustMatchers with BaseTest {

  val json: JsValue = Json.parse(jsStarWars)
  val jsCharacters = Json.parse(characters.stripMargin)

  "ScalaJq" should "test parser" in {

    val parsed: Parsed[core.Array] = parse("[.name, .place, .author.lastName]", ArrayParser.parser(_: P[_]))

    parsed mustBe Parsed.Success(Array(
        Filter(
          ArrayBuffer(
            SeqTerm(
              ArrayBuffer(
                SeqTerm(ArrayBuffer(SeqFieldTerm(ArrayBuffer(FieldTerm("name",None))))),
                SeqTerm(ArrayBuffer(SeqFieldTerm(ArrayBuffer(FieldTerm("place",None))))),
                SeqTerm(ArrayBuffer(SeqFieldTerm(ArrayBuffer(FieldTerm("author",None), FieldTerm("lastName",None)))))
              )
            )
          )
        )
    ), 33)

  }

  "ScalaJq" should "build Array" in {
    JQ(json, "[.name, .place, .author.lastName]") mustBe Json.arr("Star Wars", "in a galaxy far far away", "Lucas")
    JQ(json, "[.name,.place,.author.lastName]") mustBe Json.arr("Star Wars", "in a galaxy far far away", "Lucas")
    JQ(json, "[.name, .characters[].name]") mustBe Json.arr("Star Wars", Json.arr("Yoda", "Rey"))  //fixme : must return ["Star Wars","Yoda","Rey"]
  }

}
