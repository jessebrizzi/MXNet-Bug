import Dependencies._

name := "BugTest"

organization := "test.bug"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.11"

sbtVersion := "1.1"

libraryDependencies ++= Seq(mxnet, scrimage)
