package com.scalajq

import org.scalatest._
import play.api.libs.json._

class BasicFiltersTest extends FlatSpec with MustMatchers with BaseTest {

  val json: JsValue = Json.parse(jsStarWars)
  val jsCharacters = Json.parse(characters.stripMargin)

  "ScalaJq" should "resolve Identity" in {
    JQ(json, ".") mustBe json
  }

  "ScalaJq" should "resolve Object Identifier-Index" in {
    JQ(json, ".characters") mustBe Json.arr(
      Json.obj(
        "name" -> "Yoda",
        "appearance" -> 1980,
        "species" -> JsNull,
        "gender" -> "male"
      ),
      Json.obj(
        "name" -> "Rey",
        "appearance" -> 2015,
        "species" -> "Human",
        "gender" -> "female",
        "weapons" -> Json.arr(
          Json.obj("name" -> "Quarterstaff"),
          Json.obj("name" -> "Lightsaber"),
          Json.obj("name" -> "Blaster")
        )
      )
    )
  }

  "ScalaJq" should "resolve Array Index" in {
    JQ(jsCharacters, ".[0]") mustBe Json.obj(
      "name" -> "Yoda",
      "gender" -> "male"
    )
  }

  "ScalaJq" should "resolve Object Identifier-Index with Array Index" in {
    JQ(json, ".characters[0]") mustBe Json.obj(
      "name" -> "Yoda",
      "appearance" -> 1980,
      "species" -> JsNull,
      "gender" -> "male"
    )
  }

  "ScalaJq" should "test Optional Object Identifier-Index" in {
    intercept[IllegalArgumentException] {
      JQ(json, ".nam")
    }
    intercept[IllegalArgumentException] {
      JQ(json, ".name[1]")
    }
    JQ(json, ".name[1]?") mustBe JsNull
    JQ(json, ".nam?") mustBe JsNull
  }

  "ScalaJq" should "resolve Object Identifier-Index two times, with Array Index" in {
    JQ(json, ".characters[0].name") mustBe JsString("Yoda")
  }

  "ScalaJq" should "resolve Object Identifier-Index two times, with Array Index, with String Slice" in {
    JQ(json, ".characters[0].name[0:2]") mustBe JsString("Yo")
  }

  "ScalaJq" should "resolve Object Identifier-Index two times" in {
    JQ(json, ".author.lastName") mustBe JsString("Lucas")
  }

  "ScalaJq" should "resolve Object Identifier-Index three times" in {
    JQ(json, ".author.born.year") mustBe JsNumber(1944)
  }

  "ScalaJq" should "resolve Object Identifier-Index three times, with two Array Index" in {
    JQ(json, ".characters[1].weapons[2].name") mustBe JsString("Blaster")
  }

  "ScalaJq" should "resolve Generic Object Index" in {
    JQ(json, """.["name"]""") mustBe JsString("Star Wars")
  }

  "ScalaJq" should "resolve Array Slice" in {
    JQ(jsCharacters, ".[1:4]") mustBe Json.arr(
        Json.obj("name" -> "Rey", "gender" -> "female"),
        Json.obj("name" -> "Obi-Wan Kenobi", "gender" -> "male"),
        Json.obj("name" -> "Luke Skywalker", "gender" -> "male")
    )
  }

  "ScalaJq" should "resolve Object Identifier-Index two times, with Array Index and Array Slice" in {
    JQ(json, ".characters[1].weapons[0:2]") mustBe Json.arr(
      Json.obj("name"-> "Quarterstaff"),
      Json.obj("name"-> "Lightsaber")
    )
  }

  "ScalaJq" should "resolve Object Identifier-Index two times, with Array Index and Array Slice empty" in {
    JQ(json, ".characters[1].weapons[]") mustBe Json.arr(
      Json.obj("name"-> "Quarterstaff"),
      Json.obj("name"-> "Lightsaber"),
      Json.obj("name"-> "Blaster")
    )
  }

  "ScalaJq" should "resolve Object Identifier-Index separated by a comma" in {
    JQ(json, ".name, .author.lastName, .author.born.year, .place") mustBe
      Json.arr("Star Wars", "Lucas", 1944, "in a galaxy far far away")
  }

//  "ScalaJq" should "resolve | operator combining two filters" in {
//    JQ(jsCharacters, ".[] | .name") mustBe
//      Json.arr("Yoda", "Rey", "Obi-Wan Kenobi", "Luke Skywalker", "Han Solo")
//  }

}
