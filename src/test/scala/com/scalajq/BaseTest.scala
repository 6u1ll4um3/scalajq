package com.scalajq

trait BaseTest {

  val jsStarWars: String =
    s"""
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
       |      "gender": "Female",
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
     """.stripMargin

  val characters: String =
    s"""[
      {
        "name": "Yoda"
      },
      {
        "name": "Rey"
      },
      {
        "name": "Obi-Wan Kenobi"
      },
      {
        "name": "Luke Skywalker"
      },
      {
        "name": "Han Solo"
      }
    ]""".stripMargin


}
