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

lazy val `auth` = (project in file("auth"))
  .settings(
    name := "auth"
  )


lazy val root = (project in file("."))
  .settings(name := "songApi")
  .aggregate(`song-api`, `file-api`, `enricher-api`)
  .dependsOn(`song-api`, `file-api`, `enricher-api`)

libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % "1.11.1"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % "1.10.15"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.10.15"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"

