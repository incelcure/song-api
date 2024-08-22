lazy val `file-api` = (project in file("."))
  .settings(
    name := "file-api",
    libraryDependencies ++= scalaTest
  )

lazy val scalaTest = Seq{
  "org.scalatest" %% "scalatest" % "3.2.19" % "test"
}

libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.12.765"