package com.scalajq.core

import argonaut.Argonaut.JsonBoolean
import fastparse.NoWhitespace._
import fastparse.{CharIn, CharsWhile, CharsWhileIn, P, _}

object Operator {

  def `catch`[_: P]: P[Unit]      = P("catch")
  def `def`[_: P]: P[Unit]        = P("def")
  def `else`[_: P]: P[Unit]       = P("else")
  def `false`[_: P]: P[Unit]      = P("false")
  def `if`[_: P]: P[Unit]         = P("if")
  def `import`[_: P]: P[Unit]     = P("import")
  def `null`[_: P]: P[Unit]       = P("null")
  def `then`[_: P]: P[Unit]       = P("then")
  def `true`[_: P]: P[Unit]       = P("true")
  def `try`[_: P]: P[Unit]        = P("try")
  def alternation[_: P]: P[Unit]  = P("?//")
  def and[_: P]: P[Unit]          = P("and")
  def as[_: P]: P[Unit]           = P("as")
  def break[_: P]: P[Unit]        = P("break")
  def definedor[_: P]: P[Unit]    = P("//")
  def elif[_: P]: P[Unit]         = P("elif")
  def end[_: P]: P[Unit]          = P("end")
  def eq[_: P]: P[Unit]           = P("==")
  def foreach[_: P]: P[Unit]      = P("foreach")
  def greatereq[_: P]: P[Unit]    = P(">=")
  def include[_: P]: P[Unit]      = P("include")
  def label[_: P]: P[Unit]        = P("label")
  def lesseq[_: P]: P[Unit]       = P("<=")
  def loc[_: P]: P[Unit]          = P("__loc__")
  def module[_: P]: P[Unit]       = P("module")
  def neq[_: P]: P[Unit]          = P("!==")
  def or[_: P]: P[Unit]           = P("or")
  def rec[_: P]: P[Unit]          = P("..")
  def reduce[_: P]: P[Unit]       = P("reduce")
  def setdefinedor[_: P]: P[Unit] = P("//=")
  def setdiv[_: P]: P[Unit]       = P("/=")
  def setminus[_: P]: P[Unit]     = P("-=")
  def setmod[_: P]: P[Unit]       = P("%=")
  def setmult[_: P]: P[Unit]      = P("*=")
  def setpipe[_: P]: P[Unit]      = P("|=")
  def setplus[_: P]: P[Unit]      = P("+=")
  def dot[_: P]: P[Unit]          = P(".")

  def enterBracket[_: P]: P[String]   = CharIn("[{(").!
  def exitBracket[_: P]: P[String]    = CharIn("]})").!
  def op[_: P]: P[String]             = CharIn(".?=;,:|+-*/%$<>").!

  def digits[Any: P]: P[Unit]         = P( CharsWhileIn("0-9") )
  def exponent[Any: P]: P[Unit]       = P( CharIn("eE") ~ CharIn("+\\-").? ~ digits )
  def fractional[Any: P]: P[Unit]     = P( "." ~ digits )
  def integral[Any: P]: P[Unit]       = P( "0" | CharIn("1-9")  ~ digits.? )

  def stringChars(c: Char): JsonBoolean = c != '\"' && c != '\\'

  def space[Any: P]: P[Unit]            = P( CharsWhileIn(" \r\n", 0) )
  def hexDigit[Any: P]: P[Unit]         = P( CharIn("0-9a-fA-F") )
  def unicodeEscape[Any: P]: P[Unit]    = P( "u" ~ hexDigit ~ hexDigit ~ hexDigit ~ hexDigit )
  def escape[Any: P]: P[Unit]           = P( "\\" ~ (CharIn("\"/\\\\bfnrt") | unicodeEscape) )
  def strChars[Any: P]: P[Unit]         = P( CharsWhile(stringChars) )

  def format[_: P]: P[FormatModel] = "@" ~ CharIn("a-z", "A-Z", "0-9").rep(1).!.map(FormatModel)

  def num[_: P]: P[NumberModel] = P( CharIn("+\\-").? ~ integral ~ fractional.? ~ exponent.? ).!.map(x => NumberModel(x.toDouble))

  def string[_: P]: P[StringModel] = P( space ~ "\"" ~/ (strChars | escape).rep.! ~ "\"").map(StringModel)

  def field[_: P]: P[FieldModel] = P("." ~ (CharIn("a-z", "A-Z", "_") ~ CharIn("a-z", "A-Z", "0-9", "_").rep).!).map(FieldModel)

  def true_[_: P]: P[BooleanModel]   = `true`.map(_ => BooleanModel(true))
  def false_[_: P]: P[BooleanModel]  = `false`.map(_ => BooleanModel(false))

}
