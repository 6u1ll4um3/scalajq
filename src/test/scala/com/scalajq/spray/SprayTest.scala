package com.scalajq.spray

import com.scalajq.{BaseTest, JQ}
import org.scalatest._

class SprayTest extends FlatSpec with MustMatchers with BaseTest {

  import spray.json._
  import com.scalajq.spray.Mapper._

  val json = jsStarWars.parseJson

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
}
