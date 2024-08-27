lazy val `song-api` = (project in file("."))
  .settings(
    name := "song-api",
  )

lazy val doobieVersion = "1.0.0-RC4"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core"     % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-specs2"   % doobieVersion,
  "org.typelevel" %% "cats-effect" % "3.5.4",
  "org.postgresql" % "postgresql" % "42.7.3"
)