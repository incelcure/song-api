lazy val `enricher-api` = (project in file("."))
  .settings(
    name := "enricher-api",
  )

libraryDependencies +=  "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M17"
libraryDependencies +=  "org.scalatest" %% "scalatest" % "3.2.19" % "test"


