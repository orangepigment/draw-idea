package ru.orangepigment.drawidea.routes

import cats.effect.IO
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.circe.given
import org.http4s.Status as HttpStatus
import org.http4s.Method.GET
import org.http4s.client.dsl.io.*
import org.http4s.implicits.*
import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary
import ru.orangepigment.drawidea.conf.IdeaGeneratorConfig
import ru.orangepigment.drawidea.models.{Idea, Language, PartsNum, Theme}
import ru.orangepigment.drawidea.services.IdeaGenerator
import ru.orangepigment.drawidea.util.HttpSuite
import ru.orangepigment.drawidea.util.generators.*

object IdeaRoutesSuite extends HttpSuite {

  private val conf = IdeaGeneratorConfig(
    Language("en"),
    PartsNum(2),
    Map(
      Language("en") -> Vector(
        Theme("theme")
      )
    )
  )

  test("GET idea succeeds without partsNum query param") {
    forall(Gen.nonEmptyListOf(themeGen)) { themes =>
      val req    = GET(uri"/idea")
      val routes = new IdeaRoutes[IO](conf, ideaGenerator(themes)).routes
      expectHttpBodyAndStatus(routes, req)(themes.take(2), HttpStatus.Ok)
    }
  }

  test("GET idea succeeds with partsNum query param") {
    val gen = for {
      t  <- Gen.nonEmptyListOf(themeGen)
      pn <- partsNumGen
    } yield t -> pn

    forall(gen) { case (themes, partsNum) =>
      val req    = GET(uri"/idea".withQueryParam("partsNum", partsNum.asInstanceOf[Int]))
      val routes = new IdeaRoutes[IO](conf, ideaGenerator(themes)).routes
      expectHttpBodyAndStatus(routes, req)(themes.take(partsNum), HttpStatus.Ok)
    }
  }

  test("GET idea fails with partsNum query param is outside [2;5] range") {
    val gen =
      for {
        t  <- Gen.nonEmptyListOf(themeGen)
        pn <- arbitrary[Int].filter(!(2 to 5).contains(_))
      } yield t -> pn

    forall(gen) { case (themes, partsNum) =>
      val req    = GET(uri"/idea".withQueryParam("partsNum", partsNum))
      val routes = new IdeaRoutes[IO](conf, ideaGenerator(themes)).routes

      expectHttpBodyAndStatus(routes, req)("partsNum must be in [2; 5] range", HttpStatus.BadRequest)
    }
  }

  def ideaGenerator(themes: List[Theme]): IdeaGenerator[IO] =
    new TestIdeaGenerator {
      override def getIdea(language: Language, partsNum: PartsNum): IO[Idea] =
        IO.pure(Idea(themes.take(partsNum.value)))
    }

  protected class TestIdeaGenerator extends IdeaGenerator[IO] {
    def getIdea(language: Language, partsNum: PartsNum): IO[Idea] = ???
  }

}
