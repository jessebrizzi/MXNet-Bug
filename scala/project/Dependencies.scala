import sbt._

object Dependencies {
  lazy val mxnet = "org.apache.mxnet" % "mxnet-full_2.11-linux-x86_64-gpu" % "1.2.0"
  //lazy val mxnet = "org.apache.mxnet" % "mxnet-full_2.11-osx-x86_64-cpu" % "1.2.0"
  lazy val scrimage = "com.sksamuel.scrimage" %% "scrimage-core" % "2.1.8"
}
