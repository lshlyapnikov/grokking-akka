name := "grokking-akka"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.0",
  "com.typesafe.akka" %% "akka-stream" % "2.5.0",
  "com.typesafe.akka" %% "akka-http" % "10.0.5"
)