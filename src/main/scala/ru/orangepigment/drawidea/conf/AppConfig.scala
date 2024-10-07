package ru.orangepigment.drawidea.conf

import zio.config.*
import zio.Config
import zio.config.magnolia.*

final case class AppConfig(
    httpServer: HttpServerConfig,
    ideaGenerator: IdeaGeneratorConfig
)

object AppConfig {
  val config: Config[AppConfig] =
    deriveConfig[AppConfig].nested("AppConfig")
}
