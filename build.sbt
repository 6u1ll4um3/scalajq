name := "scalajq"

organization := "com"

version := "0.0.1-SNAPSHOT"

description := "A scala implementation of jq"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.6.8",
  "io.spray" %%  "spray-json" % "1.3.5",
  "com.lihaoyi" %% "fastparse" % "2.1.3",
  "io.argonaut" %% "argonaut" % "6.2.2",
  "org.scalaz" %% "scalaz-core" % "7.2.28",
  "org.scalatest" %% "scalatest" % "3.0.4",
  "org.specs2" %% "specs2-mock" % "4.0.3" % "test"
)