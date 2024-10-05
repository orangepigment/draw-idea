package ru.orangepigment.drawidea.routes

import cats.Monad
import org.http4s.{HttpRoutes, Request}
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Accept-Language`
import org.http4s.server.Router
import ru.orangepigment.drawidea.conf.IdeaGeneratorConfig
import ru.orangepigment.drawidea.models.{Language, PartsNum}
import ru.orangepigment.drawidea.services.IdeaGenerator
import io.github.iltotore.iron.circe.given

final class IdeaRoutes[F[_]: Monad](
    conf: IdeaGeneratorConfig,
    ideaGen: IdeaGenerator[F]
) extends Http4sDsl[F] {
  private val prefixPath = "/idea"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case request @ GET -> Root :? IdeaPartsNumParam(maybePartsNum) =>
      maybePartsNum match
        case Some(validated) =>
          validated.fold(
            parseFailures => BadRequest("partsNum must be in [2; 5] range"),
            partsNum => ok(request, partsNum)
          )
        case None => ok(request, conf.defaultPartsNum)
  }

  private def ok(request: Request[F], partsNum: PartsNum) =
    Ok(
      ideaGen.getIdea(
        request.headers
          .get[`Accept-Language`]
          .map(al => Language.applyUnsafe(al.values.head.primaryTag))
          .getOrElse(conf.defaultLanguage),
        partsNum
      )
    )

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}
