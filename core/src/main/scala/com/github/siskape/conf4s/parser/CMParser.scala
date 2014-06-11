package com.github.siskape.conf4s.parser

import org.parboiled2._

/**
 * TODO documentation
 * TODO make some nice documentation
 * @param input
 */
class CMParser(val input: ParserInput) extends Parser with StringBuilding {

  import CharPredicate.{Digit, Digit19, HexDigit, AlphaNum}

  def deb(in: String) = rule {
    run(println(s"$in ch: ${cursorChar} + i: ${cursor} vs: ${valueStack}"))
  }

  def CM: Rule1[Seq[Line]] = rule {
    WhiteSpace ~ lines ~ EOI
  }

  def lines: Rule1[Seq[Line]] = rule {
    zeroOrMore(cmTriple | newLine | comment)
  }

  def cmTriple: Rule1[CMTrie] = rule {
    cmKey ~ cmOperand ~ cmValue ~> (CMTrie(_, _, _))
  }

  def newLine: Rule1[NewLine] = rule {
    WhiteSpace ~ NewLineChar ~ push(NewLine())
  }

  def comment: Rule1[Comment] = rule {
    WhiteSpace ~ oneOrMore(CommentChar) ~ clearSB() ~ Characters ~ &(NewLineChar | EOI) ~ push(Comment(sb.toString))
  }

  // -- left side - keys
  def cmKey: Rule1[Key] = rule {
    optional(WhiteSpace) ~ cmKeyUnwrapped ~> (Key(_))
  }

  def cmKeyUnwrapped: Rule1[String] = rule {
    !('"' | opStrings) ~ clearSB() ~ keyChars ~ &(keyTerm) ~ WhiteSpace ~ push(sb.toString)
  }

  def keyTerm = rule {
    WhiteSpace ~ opStrings
  }

  // -- right side

  def cmObject: Rule1[CMObject] = rule {
    ws('{') ~ lines ~ ws('}') ~> (CMObject(_))
  }

  def cmValue: Rule1[CMValue] = rule {
    cmString | cmNumber | cmObject | cmArray | cmTrue | cmFalse | cmNull
  }

  def cmArray = rule {
    ws('[') ~ zeroOrMore(cmValue).separatedBy(ws(',')) ~ ws(']') ~> (CMArray(_))
  }

  // -- standard types
  def cmString: Rule1[CMString] = rule {
    cmStringUnwrapped ~> (CMString(_))
  }

  def cmStringUnwrapped: Rule1[String] = rule {
    '"' ~ clearSB() ~ Characters ~ ws('"') ~ push(sb.toString)
  }

  def cmNumber: Rule1[CMNumber] = rule {
    capture(Integer ~ optional(Frac) ~ optional(Exp)) ~> (CMNumber(_)) ~ WhiteSpace
  }

  def keyChars = rule {
    zeroOrMore(keyChar | '\\' ~ EscapedChar)
  }

  def keyChar = rule {
    KeyChars ~ appendSB
  }

  // basic
  def Characters = rule {
    zeroOrMore(NormalChar | '\\' ~ EscapedChar)
  }

  def NormalChar: Rule0 = rule {
    !(QuoteBackslash | NewLineChar) ~ ANY ~ appendSB()
  }

  def EscapedChar = rule(
    QuoteSlashBackSlash ~ appendSB()
      | 'b' ~ appendSB('\b')
      | 'f' ~ appendSB('\f')
      | 'n' ~ appendSB('\n')
      | 'r' ~ appendSB('\r')
      | 't' ~ appendSB('\t')
      | Unicode ~> { code => sb.append(code.asInstanceOf[Char]); ()}
  )

  def Unicode: Rule1[Int] = rule {
    'u' ~ capture(HexDigit ~ HexDigit ~ HexDigit ~ HexDigit) ~> ((s: String) => java.lang.Integer.parseInt(s, 16))
  }

  def Integer = rule {
    optional('-') ~ (Digit19 ~ Digits | Digit)
  }

  def Digits = rule {
    oneOrMore(Digit)
  }

  def Frac = rule {
    "." ~ Digits
  }

  def Exp = rule {
    ignoreCase('e') ~ optional(anyOf("+-")) ~ Digits
  }

  def cmTrue = rule {
    "true" ~ WhiteSpace ~ push(CMTrue)
  }

  def cmFalse = rule {
    "false" ~ WhiteSpace ~ push(CMFalse)
  }

  def cmNull = rule {
    "null" ~ WhiteSpace ~ push(CMNull)
  }

  def cmOperand: Rule1[Op] = rule {
    capture(opStrings) ~ WhiteSpace ~> (Op(_))
  }

  def opStrings = rule {
    "=" | ":" | "+="
  }

  def WhiteSpace: Rule0 = rule {
    zeroOrMore(WhiteSpaceChar)
  }

  def WhiteSpace1plus: Rule0 = rule {
    oneOrMore(WhiteSpaceChar)
  }

  def spaceOrMore: Rule0 = rule {
    ws(' ')
  }

  def ws(c: Char): Rule0 = rule {
    c ~ WhiteSpace
  }

  val NewLineChar = CharPredicate("\n\r\f")
  val StdWhiteChar = CharPredicate(" \t")

  val CommentChar = CharPredicate("#")
  val KeyChars = CharPredicate("_-.") ++ AlphaNum

  val WhiteSpaceChar = CharPredicate(" \t")
  val QuoteBackslash = CharPredicate("\"\\")
  val QuoteSlashBackSlash = QuoteBackslash ++ "/"
}