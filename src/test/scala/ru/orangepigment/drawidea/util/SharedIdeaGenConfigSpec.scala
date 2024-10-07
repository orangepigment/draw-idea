package ru.orangepigment.drawidea.util

import io.github.iltotore.iron.*
import ru.orangepigment.drawidea.conf.IdeaGeneratorConfig
import ru.orangepigment.drawidea.models.{Language, PartsNum, Theme}
import zio.*
import zio.test.*

abstract class SharedIdeaGenConfigSpec extends ZIOSpec[IdeaGeneratorConfig] {
  override val bootstrap: ZLayer[Any, Nothing, IdeaGeneratorConfig] =
    ZLayer.succeed(
      IdeaGeneratorConfig(
        Language("en"),
        PartsNum(2),
        Map(
          Language("en") -> Vector(
            "a",
            "b",
            "c",
            "d",
            "e"
          ).map(Theme(_)),
          Language("ru") -> Vector(
            "а",
            "б",
            "в",
            "г",
            "д"
          ).map(Theme(_))
        )
      )
    )
}
