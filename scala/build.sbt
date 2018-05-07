import Dependencies._

lazy val root = (project in file("."))
  .settings(
    organization in ThisBuild := "test.bug",
    scalaVersion in ThisBuild := "2.11.11",
    version      in ThisBuild := "0.1.0-SNAPSHOT",
    name := "BugTest",
    libraryDependencies ++= Seq(mxnet, scrimage)
  )
