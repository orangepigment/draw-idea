package ru.orangepigment.drawidea.routes

import io.github.iltotore.iron.zioJson.given
import ru.orangepigment.drawidea.conf.IdeaGeneratorConfig
import ru.orangepigment.drawidea.models.{Language, PartsNum}
import ru.orangepigment.drawidea.services.IdeaGenerator
import zio.ZIO
import zio.http.*
import zio.http.Header.AcceptLanguage
import zio.json.*

import scala.util.{Failure, Success, Try}

object IdeaRoutes:
  def apply(): Routes[IdeaGeneratorConfig & IdeaGenerator, Response] =
    Routes(
      // GET /idea
      Method.GET / Root / "v1/idea" -> handler { (req: Request) =>
        for {
          config <- ZIO.service[IdeaGeneratorConfig]
          language =
            req.headers
              .get(AcceptLanguage)
              .map {
                case AcceptLanguage.Single(language, weight) => Language.applyUnsafe(language)
                case AcceptLanguage.Multiple(languages) =>
                  config.defaultLanguage // ToDo: handle multiple languages case
                case AcceptLanguage.Any => config.defaultLanguage
              }
              .getOrElse(config.defaultLanguage)
          maybeRawPartsNum = req.url.queryParam("partsNum")
          res <-
            maybeRawPartsNum match
              case Some(rawPartsNum) =>
                Try(rawPartsNum.toInt) match
                  case Failure(_) => ZIO.fail(Response.badRequest("partsNum must be in [2; 5] range"))
                  case Success(intPartsNum) =>
                    PartsNum
                      .either(intPartsNum)
                      .fold(
                        _ => ZIO.fail(Response.badRequest("partsNum must be in [2; 5] range")),
                        idea(language, _)
                      )
              case None => idea(language, config.defaultPartsNum)
        } yield res
      }
    )

  private def idea(language: Language, partsNum: PartsNum): ZIO[IdeaGenerator, Response, Response] =
    IdeaGenerator
      .getIdea(
        language,
        partsNum
      )
      .mapBoth(
        e => Response.fromThrowable(e),
        idea => Response.json(idea.toJson)
      )
