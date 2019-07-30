package com.scalajq.play

import com.scalajq.{BaseTest, JQ}
import org.scalatest._

class PlayTest extends FlatSpec with MustMatchers with BaseTest {

  import play.api.libs.json._
  import com.scalajq.play.Mapper._

  val json = Json.parse(jsStarWars)

  "ScalaJq" should "return self" in {
    JQ(json, ".") mustBe json
  }

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
        "gender" -> "Female",
        "weapons" -> Json.arr(
          Json.obj("name" -> "Quarterstaff"),
          Json.obj("name" -> "Lightsaber"),
          Json.obj("name" -> "Blaster")
        )
      )
    )
  }

  "ScalaJq" should "get node with Array Index" in {

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

  "ScalaJq" should "get meta with 2 array fields expression" in {
    JQ(json, ".characters[1].weapons[2].name") mustBe JsString("Blaster")
  }

  "ScalaJq" should "get meta with Generic Object Index" in {
    JQ(json, """.["name"]""") mustBe JsString("Star Wars")
  }

  "ScalaJq" should "get nodes with slice" in {

    val js = Json.parse(s"""
                           |[
                           |    {
                           |      "name": "Yoda"
                           |    },
                           |    {
                           |      "name": "Rey"
                           |    },
                           |    {
                           |      "name": "Obi-Wan Kenobi"
                           |    },
                           |    {
                           |      "name": "Luke Skywalker"
                           |    },
                           |    {
                           |      "name": "Han Solo"
                           |    }
                           |]
     """.stripMargin)

    JQ(js, ".[1:4]") mustBe Json.arr(
        Json.obj("name" -> "Rey"),
        Json.obj("name" -> "Obi-Wan Kenobi"),
        Json.obj("name" -> "Luke Skywalker")
    )
  }

  "ScalaJq" should "get 2 nodes with slice" in {
    JQ(json, ".characters[1].weapons[0:2]") mustBe Json.arr(
      Json.obj("name"-> "Quarterstaff"),
      Json.obj("name"-> "Lightsaber")
    )
  }
}
