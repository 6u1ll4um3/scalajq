package com.scalajq

trait BaseTest {

  val jsStarWars: String =
    s"""
       |{
       |  "name": "Star Wars",
       |  "author" : {
       |      "firstName" : "George",
       |      "lastName" : "Lucas",
       |      "born" : {
       |          "place" : "California",
       |          "year" : 1944
       |      }
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
       |      "gender": "Female"
       |    }
       |  ]
       |}
     """.stripMargin

}
