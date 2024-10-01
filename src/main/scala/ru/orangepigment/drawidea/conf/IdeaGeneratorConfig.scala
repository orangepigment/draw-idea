package ru.orangepigment.drawidea.conf

import cats.syntax.either.*
import io.github.iltotore.iron.*
import pureconfig.ConfigReader
import pureconfig.configurable.genericMapReader
import pureconfig.error.CannotConvert
import pureconfig.generic.derivation.default.*
import ru.orangepigment.drawidea.models.{Language, PartsNum, Theme}
import ru.orangepigment.drawidea.util.pureconfig.given

import scala.jdk.CollectionConverters.*

final case class IdeaGeneratorConfig(
    defaultLanguage: Language,
    defaultPartsNum: PartsNum,
    locales: Map[Language, Vector[Theme]]
) derives ConfigReader

object IdeaGeneratorConfig {
  given ConfigReader[Map[Language, Vector[Theme]]] =
    genericMapReader { key =>
      Language.either(key).leftMap(CannotConvert(key, "Language", _))
    }
}
