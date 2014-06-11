package com.github.siskape.conf4s.parser

import org.joda.time.DateTime


//or we can use ListMap
case class ConfigFile(content: Seq[Line])

trait Line

// white space
trait WS extends Line

case class NewLine(count: Int = 1) extends WS

case class Comment(text: String) extends WS


case class Key(name: String)

case class Op(op: String)


case class CMTrie(key: Key, op: Op, value: CMValue) extends Line

//object CMTrie {
//  def apply(key: String, op: String, v: CMValue): CMTrie =
//    CMTrie(Key(key), Op(op), v)
//}

//TODO CMValue nepotrebuje byt line - done
//trait CMValue extends Line
trait CMValue

case class CMArray(values: Seq[CMValue]) extends CMValue

//TODO CMObject by mal obsahovat len lines. - done
//case class CMObject(fields: Seq[CMTrie]) extends CMValue
case class CMObject(fields: Seq[Line]) extends CMValue

trait SimpleValue extends CMValue

case class Expression(exp: String) extends SimpleValue

case class CMString(in: String) extends SimpleValue

case class CMNumber(in: BigDecimal) extends SimpleValue

object CMNumber {
  def apply(n: Int) = new CMNumber(BigDecimal(n))

  def apply(n: Long) = new CMNumber(BigDecimal(n))

  def apply(n: Double) = n match {
    case n if n.isNaN => CMNull
    case n if n.isInfinity => CMNull
    case _ => new CMNumber(BigDecimal(n))
  }

  def apply(n: BigInt) = new CMNumber(BigDecimal(n))

  def apply(n: String) = new CMNumber(BigDecimal(n))
}


case class CMDate(in: DateTime) extends SimpleValue

trait CMBool extends SimpleValue

case object CMTrue extends CMBool

case object CMFalse extends CMBool

case object CMNull extends SimpleValue

/**
 * ParseResult should be
 * Seq of items
 * CM             = Seq[Line]
 * Line           = Comment || Empty Line || SyntacticBlock
 * SyntacticBlock = Key ~ op ~ value
 * value          = arrayValue | objectValue | simpleValue
 * arrayValue     = '[' ~ (value ~ (',').?).* ~ ']'
 * objectValue    = '{' ~ (value ~ (',').?).* ~ '}'
 * simpleValue    = expression | string | number | date | Boolean | null
 */