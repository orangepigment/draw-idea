import sbt.*

object Dependencies {

  object V {
    val zio       = "2.1.11"
    val zioConfig = "4.0.2"
    val zioHttp   = "3.0.1"
    val zioJson   = "0.7.3"
    val zioMock = "1.0.0-RC12"

    val cats       = "2.12.0"
    val catsEffect = "3.5.4"
    val circe      = "0.14.10"
    val http4s     = "0.23.28"
    val iron       = "2.6.0"
    val kittens    = "3.3.0"
    val log4cats   = "2.7.0"
    val logback    = "1.5.6"
  }

  object Libraries {
    val zio = "dev.zio" %% "zio" % V.zio

    val zioJson = "dev.zio" %% "zio-json" % V.zioJson

    val zioHttp = "dev.zio" %% "zio-http" % V.zioHttp

    val ironCore    = "io.github.iltotore" %% "iron"          % V.iron
    val ironZioJson = "io.github.iltotore" %% "iron-zio-json" % V.iron

    val log4cats = "org.typelevel" %% "log4cats-slf4j" % V.log4cats

    val zioConfig         = "dev.zio" %% "zio-config"          % V.zioConfig
    val zioConfigMagnolia = "dev.zio" %% "zio-config-magnolia" % V.zioConfig
    val zioConfigTypesafe = "dev.zio" %% "zio-config-typesafe" % V.zioConfig

    // Test
    val zioTest = "dev.zio" %% "zio-test" % V.zio
    val zioTestSbt = "dev.zio" %% "zio-test-sbt" % V.zio
    val zioTestMagnolia = "dev.zio" %% "zio-test-magnolia" % V.zio
    val zioHttpTestkit = "dev.zio" %% "zio-http-testkit" % V.zioHttp
    val zioMock ="dev.zio" %% "zio-mock" % V.zioMock
  }

}
