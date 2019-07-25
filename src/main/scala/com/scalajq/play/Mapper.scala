package com.scalajq.play

import argonaut.Argonaut.{jArray, jNumber, jString}
import argonaut.Json
import play.api.libs.json._
import scalaz.Isomorphism.IsoSet

object Mapper {

  implicit def isoSet: IsoSet[JsValue, Json] = new IsoSet[JsValue, Json] { self =>

    val to: JsValue => Json = {
      case JsString(s)      => jString(s)
      case JsNumber(n)      => jNumber(n)
      case JsObject(fields) => Json(fields.mapValues(self.to).toSeq: _*)
      case JsArray(values)  => jArray(values.toList.map(self.to))
      case JsNull           => Json.jNull
      case _                => throw new UnsupportedOperationException
    }

    val from: Json => JsValue = {
      case s if s.isString   => JsString(s.stringOr(throw new ClassCastException))
      case n if n.isNumber   => JsNumber(n.numberOr(throw new ClassCastException).toBigDecimal)
      case a if a.isArray    => JsArray(a.arrayOr(throw new ClassCastException).map(self.from))
      case o if o.isObject   => JsObject(o.objectOr(throw new ClassCastException).toMap.mapValues(self.from).toSeq)
      case Json.jNull         => JsNull
      case _                  => throw new UnsupportedOperationException
    }

  }
}
