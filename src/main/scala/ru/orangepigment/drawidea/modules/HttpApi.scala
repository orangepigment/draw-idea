package ru.orangepigment.drawidea.modules

import cats.effect.kernel.Async
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.server.Router
import org.http4s.server.middleware.{RequestLogger, ResponseLogger}
import ru.orangepigment.drawidea.conf.AppConfig
import ru.orangepigment.drawidea.routes.IdeaRoutes

object HttpApi {
  val v1 = "/v1"

  def make[F[_]: Async](
      appConfig: AppConfig,
      services: Services[F]
  ): HttpApi[F] =
    new HttpApi[F](appConfig, services) {}
}

sealed trait HttpApi[F[_]: Async] private (
    appConfig: AppConfig,
    services: Services[F]
) {
  private val openRoutes: HttpRoutes[F] = IdeaRoutes[F](appConfig.ideaGenerator, services.ideaGenerator).routes

  private val routes: HttpRoutes[F] = Router(
    HttpApi.v1 -> openRoutes
  )

  private val loggers: HttpApp[F] => HttpApp[F] = {
    { (http: HttpApp[F]) =>
      RequestLogger.httpApp(true, true)(http)
    } andThen { (http: HttpApp[F]) =>
      ResponseLogger.httpApp(true, true)(http)
    }
  }

  val httpApp: HttpApp[F] = loggers(routes.orNotFound)
}
