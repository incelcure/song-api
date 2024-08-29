ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"



lazy val `song-api` = (project in file("song-api"))
  .settings(
    name := "song-api"
  )

lazy val `file-api` = (project in file("file-api"))
  .settings(
    name := "file-api"
  )

lazy val `enricher-api` = (project in file("enricher-api"))
  .settings(
    name := "enricher-api"
  )

lazy val root = (project in file("."))
  .settings(name := "songTest2")
  .aggregate(`song-api`, `file-api`, `enricher-api`)
  .dependsOn(`song-api`, `file-api`, `enricher-api`)

libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % "1.11.1"

