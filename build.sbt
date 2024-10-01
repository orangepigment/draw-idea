import Dependencies.Libraries

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.5.1"

ThisBuild / organization := "ru.orangepigment"
ThisBuild / organizationName := "orangepigment"

lazy val root = (project in file("."))
  .enablePlugins(DockerPlugin)
  .enablePlugins(AshScriptPlugin)
  .settings(
    name := "draw-idea",
    scalafmtOnCompile := true,
    Docker / packageName  := "draw-idea",
    dockerBaseImage  := "openjdk:11-jre-slim-buster",
    makeBatScripts  := Seq.empty,
    dockerExposedPorts  ++= Seq(8080),
    dockerUpdateLatest  := true,
    libraryDependencies ++= Seq(
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.log4cats,
      Libraries.pureconfigCore,
      Libraries.pureconfigCatsEffect,
      Libraries.kittens,
      Libraries.ironCore,
      //Libraries.ironPureconfig,
      Libraries.ironCats,
      Libraries.ironCirce,
      Libraries.http4sDsl,
      Libraries.http4sServer,
      Libraries.http4sClient,
      Libraries.http4sCirce,
      Libraries.logback % Runtime,
      Libraries.weaverCats % Test,
      Libraries.weaverScalaCheck % Test
    )
  )
