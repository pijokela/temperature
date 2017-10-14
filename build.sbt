name := """temperature"""
organization := "io.pirkka"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "com.pi4j" % "pi4j-core" % "1.0"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "io.pirkka.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "io.pirkka.binders._"
