package com.scalajq

import org.scalatest._
import play.api.libs.json._

class ObjectTest extends FlatSpec with MustMatchers with BaseTest {

  val json: JsValue = Json.parse(jsStarWars)
  val jsCharacters = Json.parse(characters.stripMargin)

  "ScalaJq" should "build Object" in {
    JQ(json, "{name: .name, place: .place, director: .author.lastName}") mustBe Json.obj(
      "name" -> "Star Wars",
      "place" -> "in a galaxy far far away",
      "director" -> "Lucas")

    JQ(json, "{characterNames:.characters[0:2]|.name}") mustBe Json.obj(
      "characterNames" -> Json.arr("Yoda", "Rey")
    )

    JQ(json, "{ characterNames : .characters[0:2] | .name }") mustBe Json.obj(
      "characterNames" -> Json.arr("Yoda", "Rey")
    )

    JQ(json, "{name: .name, place: .place, author: .author.lastName, .author.born.year}") mustBe Json.obj(
      "name" -> "Star Wars",
      "place" -> "in a galaxy far far away",
      "author" -> Json.arr("Lucas", 1944)
    )
  }

//  "ScalaJq" should "build Object with Array" in {
//    JQ(json, "{characterNames: .characters[].name , film: [.name, .place]}") mustBe Json.obj(
//      "characterNames" -> Json.arr("Yoda", "Rey")
//    )
//  }

}
