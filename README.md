# Scala jq

[jq](https://stedolan.github.io/jq/) for Scala

Library used :
*  [com.lihaoyi.fastparse](https://github.com/lihaoyi/fastparse/)
*  [com.typesafe.play](https://www.playframework.com/)

## Supported syntax
* Identity: `.`
* Object Identifier-Index: `.foo`, `.foo.bar`
* Optional Object Identifier-Index: `.foo?`
* Generic Object Index: `.[<string>]`
* Array Index: `.[2]`
* Array/String Slice: `.[10:15]`
* Array/Object Value Iterator: `.[]`
* Comma: `,`
* Pipe: `|`
* Array Construction: `[]`
* Object Construction: `{}`
* Recursive Descent: `..`

## Usage
```scala
import play.api.libs.json.{ Json, JsValue }
import com.scalajq.JQ

val json: JsValue = Json.parse(s"""
      |{
      |  "name": "Star Wars",
      |  "place": "in a galaxy far far away",
      |  "author": {
      |    "firstName": "George",
      |    "lastName": "Lucas",
      |    "born": {
      |      "place": "California",
      |      "year": 1944
      |    }
      |  },
      |  "characters": [
      |    {
      |      "name": "Yoda",
      |      "appearance": 1980,
      |      "species": null,
      |      "gender": "male"
      |    },
      |    {
      |      "name": "Rey",
      |      "appearance": 2015,
      |      "species": "Human",
      |      "gender": "female",
      |      "weapons": [
      |        {
      |          "name": "Quarterstaff"
      |        },
      |        {
      |          "name": "Lightsaber"
      |        },
      |        {
      |          "name": "Blaster"
      |        }
      |      ]
      |    }
      |  ]
      |}
    """.stripMargin)

JQ(json, ".characters[1].weapons[2].name") // return JsString("Blaster")
JQ(json, ".characters[1].weapons[].name")  // return Json.arr("Quarterstaff", "Lightsaber", "Blaster")
JQ(json, ".characters[0:2] | .name")       // return Json.arr("Yoda", "Rey")
JQ(json, ".name, .author.lastName, .author.born.year, .place") // return Json.arr("Star Wars", "Lucas", 1944, "in a galaxy far far away")
JQ(json, "{name: .name, director: .author.lastName}") // return Json.obj("name" -> "Star Wars", "director" -> "Lucas")
JQ(json, "{film: .name, infos: [.place, .author.lastName]}") // return Json.obj("film" -> "Star Wars","infos" -> Json.arr("in a galaxy far far away","Lucas"))}

```