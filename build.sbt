import Dependencies.Libraries

ThisBuild / version := "0.2.0-SNAPSHOT"

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
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= Seq(
      Libraries.zio,
      Libraries.zioConfig,
      Libraries.zioConfigTypesafe,
      Libraries.zioConfigMagnolia,
      Libraries.ironCore,
      Libraries.ironZioJson,
      Libraries.zioHttp,
      Libraries.zioTest % Test,
      Libraries.zioTestSbt % Test,
      Libraries.zioTestMagnolia % Test,
      Libraries.zioHttpTestkit % Test,
      Libraries.zioMock % Test
    )
  )
