package ru.orangepigment.drawidea.models

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Interval.Closed
import zio.Config
import zio.config.magnolia.DeriveConfig

opaque type PartsNum <: Int = Int :| Closed[2, 5]
object PartsNum extends RefinedTypeOps[Int, Closed[2, 5], PartsNum] {
  given DeriveConfig[PartsNum] = DeriveConfig[Int]
    .mapOrFail[PartsNum](
      PartsNum.either(_).left.map(e => Config.Error.InvalidData(message = e))
    )
}

opaque type Theme <: String = String :| Pure
object Theme extends RefinedTypeOps[String, Pure, Theme] {
  given DeriveConfig[Theme] = DeriveConfig[String].map(Theme(_))
}

opaque type Language <: String = String :| Pure
object Language extends RefinedTypeOps[String, Pure, Language] {
  given DeriveConfig[Language] = DeriveConfig[String].map(Language(_))
}

opaque type Idea <: List[Theme] = List[Theme] :| Pure
object Idea extends RefinedTypeOps[List[Theme], Pure, Idea] {
  import Theme.given
  given DeriveConfig[Idea] = DeriveConfig.listDesc[Theme].map(Idea(_))
}
