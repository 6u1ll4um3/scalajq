package com.scalajq

import org.scalatest._
import play.api.libs.json._

class ArrayTest extends FlatSpec with MustMatchers with BaseTest {

  val json: JsValue = Json.parse(jsStarWars)
  val jsCharacters = Json.parse(characters.stripMargin)

  "ScalaJq" should "build Array" in {
    JQ(json, "[.name, .place, .author.lastName]") mustBe Json.arr("Star Wars", "in a galaxy far far away", "Lucas")
    JQ(json, "[.name,.place,.author.lastName]") mustBe Json.arr("Star Wars", "in a galaxy far far away", "Lucas")
    JQ(json, "[.name, .characters[].name]") mustBe Json.arr("Star Wars", Json.arr("Yoda", "Rey"))
  }

}
