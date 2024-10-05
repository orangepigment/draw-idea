package ru.orangepigment.drawidea.conf

import cats.effect.Sync
import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.generic.derivation.default.*
import pureconfig.module.catseffect.syntax.*

final case class AppConfig(
    httpServer: HttpServerConfig,
    ideaGenerator: IdeaGeneratorConfig
) derives ConfigReader

object AppConfig {
  def load[F[_]: Sync]: F[AppConfig] =
    ConfigSource.default.loadF[F, AppConfig]()
}
