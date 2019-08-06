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
     """.stripMargin

  val characters: String =
    s"""|[
        |  {
        |    "name": "Yoda",
        |    "gender": "male"
        |  },
        |  {
        |    "name": "Rey",
        |    "gender": "female"
        |  },
        |  {
        |    "name": "Obi-Wan Kenobi",
        |    "gender": "male"
        |  },
        |  {
        |    "name": "Luke Skywalker",
        |    "gender": "male"
        |  },
        |  {
        |    "name": "Han Solo",
        |    "gender": "male"
        |  }
        |]
     """.stripMargin


}
