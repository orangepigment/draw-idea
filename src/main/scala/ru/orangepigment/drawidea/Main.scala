package ru.orangepigment.drawidea

import ru.orangepigment.drawidea.conf.{AppConfig, IdeaGeneratorConfig}
import ru.orangepigment.drawidea.routes.IdeaRoutes
import ru.orangepigment.drawidea.services.{IdeaGenerator, IdeaGeneratorImpl}
import zio.*
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*

object MainApp extends ZIOAppDefault {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.setConfigProvider(
      TypesafeConfigProvider
        .fromResourcePath()
    )

  private val serverConfig: ZLayer[Any, Config.Error, Server.Config] =
    ZLayer
      .fromZIO(
        ZIO.config[AppConfig](AppConfig.config).map { c =>
          Server.Config.default.binding(c.httpServer.host, c.httpServer.port)
        }
      )

  private val ideaGeneratorConfig: ZLayer[Any, Config.Error, IdeaGeneratorConfig] =
    ZLayer
      .fromZIO(
        ZIO.config[AppConfig](AppConfig.config).map { c =>
          c.ideaGenerator
        }
      )

  def run = {
    (Server
      .install(
        IdeaRoutes()
      )
      .flatMap(port => Console.printLine(s"Started server on port: $port")) *> ZIO.never)
      .provide(
        serverConfig,
        ideaGeneratorConfig,
        // nettyConfig,
        Server.live,

        // To use the persistence layer, provide the `PersistentUserRepo.layer` layer instead
        IdeaGeneratorImpl.layer
      )
  }
}
