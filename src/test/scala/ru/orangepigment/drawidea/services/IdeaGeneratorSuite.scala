package ru.orangepigment.drawidea.services

import cats.effect.IO
import cats.effect.std.Random
import cats.syntax.semigroup.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import org.scalacheck.Gen
import ru.orangepigment.drawidea.models.{Language, LocaleNotFound, PartsNum, Theme}
import ru.orangepigment.drawidea.util.generators.partsNumGen
import weaver.SimpleIOSuite
import weaver.scalacheck.Checkers

object IdeaGeneratorSuite extends SimpleIOSuite with Checkers {
  private val locales = Map(
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

  private val gen = for {
    l  <- Gen.oneOf(List(Language("en"), Language("ru")))
    pn <- partsNumGen
  } yield l -> pn

  private val absentLocaleGen = for {
    l <- Gen.alphaNumStr
      .filter(!List("en", "ru").contains(_))
      .map(Language(_))
    pn <- partsNumGen
  } yield l -> pn

  test("get idea successfully") {
    forall(gen) { case (lang, partsNum) =>
      Random.scalaUtilRandom[IO].flatMap { case given Random[IO] =>
        IdeaGenerator
          .make[IO](locales)
          .getIdea(lang, partsNum)
          .map(idea =>
            expect.same(partsNum, idea.length) |+|
              expect.same(idea.distinct.length, idea.length) |+|
              expect(idea.forall(locales(lang).contains))
          )
      }
    }
  }

  test("locale not found") {
    forall(absentLocaleGen) { case (lang, partsNum) =>
      Random.scalaUtilRandom[IO].flatMap { case given Random[IO] =>
        IdeaGenerator
          .make[IO](locales)
          .getIdea(lang, partsNum)
          .attempt
          .map {
            case Left(LocaleNotFound(notFoundLang)) => expect.same(lang, notFoundLang)
            case _ =>
              failure("Expected locale not found error")
          }
      }
    }
  }

}
