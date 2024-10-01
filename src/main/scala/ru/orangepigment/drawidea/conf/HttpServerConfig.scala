package ru.orangepigment.drawidea.conf

import cats.syntax.either.*
import com.comcast.ip4s.{Host, Port}
import pureconfig.ConfigReader
import pureconfig.error.{CannotConvert, FailureReason}
import pureconfig.generic.derivation.default.*

final case class HttpServerConfig(
    host: Host,
    port: Port
) derives ConfigReader

object HttpServerConfig {
  given ConfigReader[Host] =
    ConfigReader.fromString(s =>
      Host.fromString(s).fold(CannotConvert(s, "Host", "Invalid host format").asLeft[Host])(_.asRight[FailureReason])
    )

  given ConfigReader[Port] =
    ConfigReader.intConfigReader.emap(i =>
      Port
        .fromInt(i)
        .fold(CannotConvert(i.toString, "Port", "Port must be in 0 to 65535 range").asLeft[Port])(
          _.asRight[FailureReason]
        )
    )
}
