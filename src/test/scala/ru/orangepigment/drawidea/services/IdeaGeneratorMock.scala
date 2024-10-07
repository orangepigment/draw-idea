package ru.orangepigment.drawidea.services

import ru.orangepigment.drawidea.models.{Idea, Language, PartsNum}
import zio.*
import zio.mock.*

object IdeaGeneratorMock extends Mock[IdeaGenerator] {
  object GetIdea extends Effect[(Language, PartsNum), Throwable, Idea]

  val compose: URLayer[Proxy, IdeaGenerator] =
    ZLayer {
      for {
        proxy <- ZIO.service[Proxy]
      } yield new IdeaGenerator {
        def getIdea(language: Language, partsNum: PartsNum): ZIO[Any, Throwable, Idea] =
          proxy(GetIdea, language, partsNum)
      }
    }
}
