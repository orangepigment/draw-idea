package ru.orangepigment.drawidea.modules

import cats.MonadThrow
import cats.effect.std.Random
import ru.orangepigment.drawidea.conf.IdeaGeneratorConfig
import ru.orangepigment.drawidea.services.IdeaGenerator

object Services {
  def make[F[_]: MonadThrow: Random](
      ideaGeneratorConfig: IdeaGeneratorConfig
  ): Services[F] = {
    new Services[F](
      ideaGenerator = IdeaGenerator.make[F](ideaGeneratorConfig.locales)
    ) {}
  }
}

sealed trait Services[F[_]] private (
    val ideaGenerator: IdeaGenerator[F]
)
