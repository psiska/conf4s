package com.github.siskape.conf4s.parser

import org.parboiled2._

import scala.util.{Success, Failure}

object Pokus {
  def main(in: Array[String]): Unit = {
    val data1 =
      """p1 = "q1"
        |r2 = "z2"
        |
        | c1 :"d4"
        | c2 += "d5"
        |
        | # juu
        | e1.ee2 = "sdf"
        |a1="b2"
        |###hop""".stripMargin

    val data2 = "\n\n\np1 = \"q1\""

    val data3 = "\n\n\n\n"
    val data4 = "a1= [\"a\", \"b\", \"c\"]"

    val data5 = "a1 = { a = \"b\", c = \"d\"}"
    val data6 = "p1 = \"p1\"\np2 = \"p2\"\np3 = 1\np4 = 1.1\np5 = true\np6 = false\np7 = null"
    val data7 = "a1 = {\n a = \"b\"\n #hop\nc = \"d\"}"

    val cmParser = new CMParser(data7)

    cmParser.CM.run() match {
      case Success(a) => println("Expression is valid " + a)
      case Failure(e: ParseError) ⇒ println("Expression is not valid: " + cmParser.formatError(e, showExpected = true, showLine = true, showPosition = true, showTraces = true))
      case Failure(e) => println("Unexpected error during parsing run: " + e)
    }


  }
}