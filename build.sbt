name := """PlayGameServerWS"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.0"

val akkaVersion = "2.5.25"
val akkaHttpVersion = "10.1.9"

libraryDependencies += guice
libraryDependencies += ws

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http
libraryDependencies += "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http-testkit
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http-spray-json
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
