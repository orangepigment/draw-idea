package ru.orangepigment.drawidea.conf

import io.github.iltotore.iron.*
import ru.orangepigment.drawidea.models.{Language, PartsNum, Theme}
import zio.config.*
import zio.Config
import zio.config.magnolia.{DeriveConfig, deriveConfig}

final case class IdeaGeneratorConfig(
    defaultLanguage: Language,
    defaultPartsNum: PartsNum,
    locales: Map[Language, Vector[Theme]]
)

object IdeaGeneratorConfig {
  given DeriveConfig[Map[Language, Vector[Theme]]] =
    DeriveConfig.mapDesc[Vector[Theme]].map(c => c.map { case (k, v) => Language.refineUnsafe(k) -> v })

  given Config[IdeaGeneratorConfig] =
    deriveConfig[IdeaGeneratorConfig] // .nested("IdeaGeneratorConfig")
}
