lazy val `song-api` = (project in file("."))
  .settings(
    name := "song-api",
  )

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
  "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC2",
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC2",
  "org.tpolecat" %% "doobie-specs2" % "1.0.0-RC2" % "test",
  "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC2" % "test"
)