package ru.orangepigment.drawidea

import cats.effect.std.{Random, Supervisor}
import cats.effect.{IO, IOApp, Resource}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import ru.orangepigment.drawidea.conf.AppConfig
import ru.orangepigment.drawidea.modules.{HttpApi, Services}
import ru.orangepigment.drawidea.resources.MkHttpServer

object Main extends IOApp.Simple {
  given Logger[IO] = Slf4jLogger.getLogger[IO]

  given IO[Random[IO]] = Random.scalaUtilRandom[IO]

  override def run: IO[Unit] =
    AppConfig.load[IO].flatMap { cfg =>
      Logger[IO].info(s"Loaded config $cfg") >>
        Supervisor[IO].use { case given Supervisor[IO] =>
          Resource
            .eval(Random.scalaUtilRandom[IO])
            .map { case given Random[IO] =>
              val services = Services.make[IO](cfg.ideaGenerator)
              val api      = HttpApi.make[IO](cfg, services)

              cfg.httpServer -> api.httpApp
            }
            .flatMap { case (cfg, httpApp) =>
              MkHttpServer[IO].newEmber(cfg, httpApp)
            }
            .useForever
        }
    }
}
