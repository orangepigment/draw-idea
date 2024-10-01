package ru.orangepigment.drawidea.routes

import org.http4s.dsl.io.OptionalValidatingQueryParamDecoderMatcher
import ru.orangepigment.drawidea.models.PartsNum
import ru.orangepigment.drawidea.models.PartsNum.given

object IdeaPartsNumParam extends OptionalValidatingQueryParamDecoderMatcher[PartsNum]("partsNum")
