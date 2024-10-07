package ru.orangepigment.drawidea.services

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import ru.orangepigment.drawidea.conf.IdeaGeneratorConfig
import ru.orangepigment.drawidea.models.{Idea, Language, LocaleNotFound, PartsNum, Theme}
import zio.{Random, ZIO, ZLayer}

trait IdeaGenerator {
  def getIdea(language: Language, partsNum: PartsNum): ZIO[Any, Throwable, Idea]
}

object IdeaGenerator {
  def getIdea(language: Language, partsNum: PartsNum): ZIO[IdeaGenerator, Throwable, Idea] =
    ZIO.serviceWithZIO[IdeaGenerator](_.getIdea(language, partsNum))
}

final case class IdeaGeneratorImpl(locales: Map[Language, Vector[Theme]]) extends IdeaGenerator {
  override def getIdea(language: Language, partsNum: PartsNum): ZIO[Any, Throwable, Idea] =
    locales.get(language) match
      case Some(themes) =>
        getTheme(themes, partsNum)
      case None => ZIO.fail(LocaleNotFound(language))

  private def getTheme(themes: Vector[Theme], counter: Int, acc: List[Theme] = List.empty): ZIO[Any, Nothing, Idea] = {
    if counter == 0 then ZIO.succeed(Idea(acc))
    else
      Random.nextIntBounded(themes.length).flatMap { i =>
        val theme = themes(i)
        if acc.contains(theme) then getTheme(themes, counter, acc)
        else getTheme(themes, counter - 1, theme +: acc)
      }
  }
}

object IdeaGeneratorImpl {
  val layer: ZLayer[IdeaGeneratorConfig, Nothing, IdeaGenerator] =
    ZLayer {
      for {
        config <- ZIO.service[IdeaGeneratorConfig]
      } yield IdeaGeneratorImpl(config.locales)
    }
}
