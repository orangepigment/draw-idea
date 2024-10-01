package ru.orangepigment.drawidea.util

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import org.scalacheck.Gen
import ru.orangepigment.drawidea.models.{PartsNum, Theme}

object generators {

  val partsNumGen: Gen[PartsNum] =
    Gen.chooseNum(2, 5).map(i => PartsNum(i.refineUnsafe))

  val themeGen: Gen[Theme] =
    Gen
      .chooseNum(3, 12)
      .flatMap { n =>
        Gen.buildableOfN[String, Char](n, Gen.alphaChar)
      }
      .map(Theme(_))

}
