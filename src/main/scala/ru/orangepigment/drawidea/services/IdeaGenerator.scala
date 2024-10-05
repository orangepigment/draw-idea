package ru.orangepigment.drawidea.services

import cats.{Monad, MonadThrow}
import cats.effect.std.Random
import cats.syntax.applicative.*
import cats.syntax.applicativeError.*
import cats.syntax.either.*
import cats.syntax.functor.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import ru.orangepigment.drawidea.models.{Idea, Language, LocaleNotFound, PartsNum, Theme}

trait IdeaGenerator[F[_]] {
  def getIdea(language: Language, partsNum: PartsNum): F[Idea]
}

object IdeaGenerator {
  def make[F[_]: MonadThrow: Random](
      locales: Map[Language, Vector[Theme]]
  ): IdeaGenerator[F] = new IdeaGenerator[F]:
    def getIdea(language: Language, partsNum: PartsNum): F[Idea] =
      locales.get(language) match
        case Some(themes) =>
          getTheme(themes, partsNum)
        case None => LocaleNotFound(language).raiseError[F, Idea]

    private def getTheme(themes: Vector[Theme], partsNum: PartsNum): F[Idea] = {
      Monad[F]
        .tailRecM[(Int, List[Theme]), List[Theme]](partsNum -> List.empty[Theme]) {
          case (counter, acc) if counter == 0 => acc.asRight[(Int, List[Theme])].pure[F]
          case (counter, acc) =>
            Random[F]
              .betweenInt(0, themes.length)
              .map(i =>
                val theme = themes(i)
                if acc.contains(theme) then (counter -> acc).asLeft[List[Theme]]
                else (counter - 1                    -> (theme +: acc)).asLeft[List[Theme]]
              )
        }
        .map(l => Idea(l))

    }
}
