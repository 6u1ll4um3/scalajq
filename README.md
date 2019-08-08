#Scala jq

[jq](https://stedolan.github.io/jq/) for Scala

##Using 
*  [com.lihaoyi.fastparse](https://github.com/lihaoyi/fastparse/)
*  [com.typesafe.play](https://www.playframework.com/)

##Supported syntax
* Identity: `.`
* Object Identifier-Index: `.foo`, `.foo.bar`
* Optional Object Identifier-Index: `.foo?`
* Generic Object Index: `.[<string>]`
* Array Index: `.[2]`
* Array/String Slice: `.[10:15]`
* Array/Object Value Iterator: `.[]`
* Comma: `,`

##Usage
```scala
import play.api.libs.json.{ Json, JsValue }
import com.scalajq.JQ

val json: JsValue = Json.parse(s"""
    |{
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

```