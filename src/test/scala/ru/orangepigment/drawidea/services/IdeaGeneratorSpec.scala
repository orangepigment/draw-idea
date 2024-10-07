package ru.orangepigment.drawidea.services

import io.github.iltotore.iron.*
import ru.orangepigment.drawidea.conf.IdeaGeneratorConfig
import ru.orangepigment.drawidea.models.{Language, LocaleNotFound, Theme}
import ru.orangepigment.drawidea.util.SharedIdeaGenConfigSpec
import ru.orangepigment.drawidea.util.generators.partsNumGen
import zio.*
import zio.test.*

object IdeaGeneratorSpec extends SharedIdeaGenConfigSpec {

  def spec =
    suiteAll("IdeaGeneratorSpec") {
      val localeGen: Gen[Any, Language] =
        Gen.fromIterable(List(Language("en"), Language("ru")))

      val absentLocaleGen =
        Gen
          .string1(Gen.alphaChar)
          .filter(!List("en", "ru").contains(_))
          .map(Language(_))

      suite("fixture")(
        test("get idea successfully") {
          check(localeGen, partsNumGen) { case (lang, partsNum) =>
            for {
              config <- ZIO.service[IdeaGeneratorConfig]
              idea   <- IdeaGenerator.getIdea(lang, partsNum)
            } yield assertTrue(
              partsNum == idea.length,
              idea.distinct.length == idea.length,
              idea.forall(config.locales(lang).contains)
            )
          }
        },
        test("locale not found") {
          check(absentLocaleGen, partsNumGen) { case (lang, partsNum) =>
            for {
              exit <- IdeaGenerator.getIdea(lang, partsNum).exit
            } yield assertTrue(exit.is(_.failure) == LocaleNotFound(lang))
          }
        }
      ).provideShared(bootstrap, IdeaGeneratorImpl.layer)
    }

}
