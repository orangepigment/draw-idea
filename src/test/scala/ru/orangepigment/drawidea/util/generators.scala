package ru.orangepigment.drawidea.util

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import ru.orangepigment.drawidea.models.{PartsNum, Theme}
import zio.test.Gen

object generators {

  val partsNumGen: Gen[Any, PartsNum] =
    Gen.int(2, 5).map(i => PartsNum(i.refineUnsafe))

  val themeGen: Gen[Any, Theme] =
    Gen
      .stringBounded(3, 12)(Gen.alphaChar)
      .map(Theme(_))

  val themeListGen: Gen[Any, List[Theme]] =
    Gen.listOfBounded(5, 100)(themeGen)
}
