import sbt.*

object Dependencies {

  object V {
    val cats          = "2.12.0"
    val catsEffect    = "3.5.4"
    val circe         = "0.14.10"
    val http4s        = "0.23.28"
    val iron          = "2.6.0"
    val kittens       = "3.3.0"
    val log4cats      = "2.7.0"
    val logback       = "1.5.6"
    val pureconfig       = "0.17.7"
    val weaver        = "0.8.4"
  }

  object Libraries {
    def circe(artifact: String): ModuleID  = "io.circe"   %% s"circe-$artifact"  % V.circe
    def http4s(artifact: String): ModuleID = "org.http4s" %% s"http4s-$artifact" % V.http4s
    def pureconfig(artifact: String): ModuleID = "com.github.pureconfig" %% s"pureconfig-$artifact" % V.pureconfig

    val cats       = "org.typelevel"    %% "cats-core"   % V.cats
    val catsEffect = "org.typelevel"    %% "cats-effect" % V.catsEffect
    val kittens    = "org.typelevel" %% "kittens" % V.kittens

    val circeCore    = circe("core")

    val http4sDsl    = http4s("dsl")
    val http4sServer = http4s("ember-server")
    val http4sClient = http4s("ember-client")
    val http4sCirce  = http4s("circe")

    val ironCore  = "io.github.iltotore" %% "iron" % V.iron
    val ironCats  = "io.github.iltotore" %% "iron-cats" % V.iron
    val ironPureconfig = "io.github.iltotore" %% "iron-pureconfig" % V.iron
    val ironCirce = "io.github.iltotore" %% "iron-circe" % V.iron

    val log4cats = "org.typelevel" %% "log4cats-slf4j" % V.log4cats

    val pureconfigCore = pureconfig("core")
    val pureconfigCatsEffect = pureconfig("cats-effect")

    // Runtime
    val logback = "ch.qos.logback" % "logback-classic" % V.logback

    // Test
    val log4catsNoOp      = "org.typelevel"       %% "log4cats-noop"      % V.log4cats
    val weaverCats        = "com.disneystreaming" %% "weaver-cats"        % V.weaver
    val weaverScalaCheck  = "com.disneystreaming" %% "weaver-scalacheck"  % V.weaver
  }

}