package ru.orangepigment.drawidea.models

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Interval.Closed
import org.http4s.QueryParamDecoder

opaque type PartsNum <: Int = Int :| Closed[2, 5]
object PartsNum extends RefinedTypeOps[Int, Closed[2, 5], PartsNum] {
  given QueryParamDecoder[PartsNum] =
    QueryParamDecoder.fromUnsafeCast[PartsNum](p => PartsNum.applyUnsafe(p.value.toInt))("PartsNum")
}

opaque type Theme <: String = String :| Pure
object Theme extends RefinedTypeOps[String, Pure, Theme]

opaque type Language <: String = String :| Pure
object Language extends RefinedTypeOps[String, Pure, Language]

opaque type Idea <: List[Theme] = List[Theme] :| Pure
object Idea extends RefinedTypeOps[List[Theme], Pure, Idea]
