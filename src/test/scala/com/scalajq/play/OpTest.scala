package com.scalajq.play

import com.scalajq.{BaseTest, JQ}
import org.scalatest._

class OpTest extends FlatSpec with MustMatchers with BaseTest {

  import play.api.libs.json._
  import com.scalajq.play.Mapper._

  val json = Json.parse(jsStarWars)

  "ScalaJq" should "get node with field expression" in {

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
        "gender" -> "Female"
      )
    )
  }

  "ScalaJq" should "get node with array expression" in {

    val js = Json.parse(s"""
         |[
         |    {
         |      "name": "Yoda"
         |    },
         |    {
         |      "name": "Rey"
         |    }
         |]
     """.stripMargin)

    JQ(js, ".[0]") mustBe Json.obj(
      "name" -> "Yoda"
    )
  }

  "ScalaJq" should "get node with field array expression" in {

    JQ(json, ".characters[0]") mustBe Json.obj(
      "name" -> "Yoda",
      "appearance" -> 1980,
      "species" -> JsNull,
      "gender" -> "male"
    )
  }

  "ScalaJq" should "get meta with field array expression" in {
    JQ(json, ".characters[0].name") mustBe JsString("Yoda")
  }

  "ScalaJq" should "get node with 2 fields expression" in {
    JQ(json, ".author.lastName") mustBe JsString("Lucas")
  }

  "ScalaJq" should "get node with 3 fields expression" in {
    JQ(json, ".author.born.year") mustBe JsNumber(1944)
  }
}
