package ru.orangepigment.drawidea.routes

import io.github.iltotore.iron.*
import ru.orangepigment.drawidea.models.{Idea, Theme}
import ru.orangepigment.drawidea.services.{IdeaGenerator, IdeaGeneratorMock}
import ru.orangepigment.drawidea.services.IdeaGeneratorSpec.suiteAll
import ru.orangepigment.drawidea.util.SharedIdeaGenConfigSpec
import ru.orangepigment.drawidea.util.generators.*
import zio.*
import zio.test.*
import zio.http.*
import zio.http.netty.NettyConfig
import zio.http.netty.server.NettyDriver
import zio.mock.Expectation

object IdeaRoutesSpec extends SharedIdeaGenConfigSpec {

  def mockIdeaGenerator: ULayer[IdeaGenerator] =
    IdeaGeneratorMock
      .GetIdea(
        assertion = Assertion.anything,
        result = Expectation.valueF { case (_, partsNum) =>
          Idea(
            List(
              "a",
              "b",
              "c",
              "d",
              "e"
            ).map(Theme(_)).take(partsNum)
          )
        }
      )
      .atLeast(1)
      .toLayer

  def mockIdeaGeneratorNoCall: ULayer[IdeaGenerator] =
    IdeaGeneratorMock
      .GetIdea(
        assertion = Assertion.anything,
        result = Expectation.failure(new RuntimeException("Unexpected mock call"))
      )
      .exactly(0)
      .toLayer

  def spec = suite("IdeaRoutesSpec")(
    test("GET idea succeeds without partsNum query param") {
      for {
        client  <- ZIO.service[Client]
        _       <- TestServer.addRoutes(IdeaRoutes())
        uioPort <- ZIO.serviceWith[Server](_.port)
        port    <- uioPort
        url = URL.root.port(port)
        ideaResponse <- client(
          Request.get(url / "v1" / "idea")
        )
      } yield assertTrue(ideaResponse.status == Status.Ok)
    }.provideSome[Client & Driver](
      TestServer.layer,
      Scope.default,
      bootstrap,
      mockIdeaGenerator
    ),
    test("GET idea idea succeeds with partsNum query param") {
      check(partsNumGen) { partsNum =>
        for {
          client  <- ZIO.service[Client]
          _       <- TestServer.addRoutes(IdeaRoutes())
          uioPort <- ZIO.serviceWith[Server](_.port)
          port    <- uioPort
          url = URL.root.port(port)
          ideaResponse <- client(
            Request
              .get(url / "v1" / "idea")
              .addQueryParam("partsNum", partsNum.toString)
          )
        } yield assertTrue(ideaResponse.status == Status.Ok)
      }
    }.provideSome[Client & Driver](
      TestServer.layer,
      Scope.default,
      bootstrap,
      mockIdeaGenerator
    ),
    test("GET idea fails with partsNum query param is outside [2;5] range") {
      check(
        Gen.int.filter(!Range(2, 6).contains(_))
      ) { partsNum =>
        for {
          client  <- ZIO.service[Client]
          _       <- TestServer.addRoutes(IdeaRoutes())
          uioPort <- ZIO.serviceWith[Server](_.port)
          port    <- uioPort
          url = URL.root.port(port)
          ideaResponse <- client(
            Request
              .get(url / "v1" / "idea")
              .addQueryParam("partsNum", partsNum.toString)
          )
        } yield assertTrue(ideaResponse.status == Status.BadRequest)
      }
    }.provideSome[Client & Driver](
      TestServer.layer,
      Scope.default,
      bootstrap,
      mockIdeaGeneratorNoCall
    )
  ).provide(
    ZLayer.succeed(Server.Config.default.onAnyOpenPort),
    Client.default,
    NettyDriver.customized,
    ZLayer.succeed(NettyConfig.defaultWithFastShutdown)
  )

  /*private val conf = IdeaGeneratorConfig(
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
  }*/

}
