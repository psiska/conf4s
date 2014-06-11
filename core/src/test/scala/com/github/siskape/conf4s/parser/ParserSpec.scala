package com.github.siskape.conf4s.parser


import org.scalacheck.Prop._
import org.scalacheck.Properties

import org.specs2._, org.specs2.specification._
import org.specs2.matcher._
import scalaz._
import Scalaz._
import scala.io.Source

object TestHelper {
  def loadTestFile(fileName: String): String = {
    val inputStream = getClass.getClassLoader.getResourceAsStream(fileName)
    val src = Source.fromInputStream(inputStream)
    src.getLines.mkString("\n")
  }

  def data1 = loadTestFile("simple1.conf")

  def d1Expect: Seq[Line] = Vector(
    CMTrie(Key("p1"), Op("="), CMString("p1")),
    NewLine(),
    CMTrie(Key("p2"), Op("="), CMString("p2")),
    NewLine(),
    CMTrie(Key("p3"), Op("="), CMNumber(1)),
    NewLine(),
    CMTrie(Key("p4"), Op("="), CMNumber(1.1)),
    NewLine(),
    CMTrie(Key("p5"), Op("="), CMTrue),
    NewLine(),
    CMTrie(Key("p6"), Op("="), CMFalse),
    NewLine(),
    CMTrie(Key("p7"), Op("="), CMNull)
  )

  def data2 = loadTestFile("simple2.conf")

  def d2Expect: Seq[Line] = Vector(
    CMTrie(Key("p1"), Op("="), CMString("p1")),
    NewLine(),
    CMTrie(Key("p2"), Op(":"), CMString("p2")),
    NewLine(),
    CMTrie(Key("p3"), Op("+="), CMString("p3"))
  )

  def data3 = loadTestFile("simple3.conf")

  def d3Expect: Seq[Line] = Vector(
    CMTrie(Key("p1"), Op("="), CMString("p1")),
    NewLine(),
    Comment(" comment1"),
    NewLine(),
    CMTrie(Key("p2"), Op("="), CMString("p2")),
    Comment("comment2"),
    NewLine(),
    Comment("endcomment")
  )

  def data4 = loadTestFile("complex1.conf")

  def d4Expect: Seq[Line] = Vector(
    CMTrie(Key("p1.p2"), Op("="), CMString("p1")),
    NewLine(),
    CMTrie(Key("p1.p2.p3"), Op("="), CMString("p3")),
    NewLine(),
    NewLine(),
    CMTrie(Key("p1.p2.p4"), Op("="), CMArray(Vector(CMNumber(1), CMNumber(2), CMNumber(3), CMNumber(10)))),
    NewLine(),
    NewLine(),
    CMTrie(Key("p1.p2.p5"), Op("="), CMObject(Vector(NewLine(), CMTrie(Key("a"), Op("="), CMString("b")),NewLine(), CMTrie(Key("c"), Op("="), CMString("d")), NewLine())))
  )
}


/**
 * TODO create the correct specification for parser
 * -correct print of ast to file - must match - if not counting the tabs and spaces (leading)
 */
class ParserSpec extends Specification with ScalaCheck {

  def is = s2"""
    Specification to check the 'CMParser' capabilities.

    The 'CMParser' should parse
      simple keys, simple values                        $ee1
      various operators                                 $ee2
      comments                                          $ee3
      complex keys, arrays, objects                     $ee4
                                                        """


  def ee1 = {
    val parser = new CMParser(TestHelper.data1)
    parser.CM.run().get mustEqual TestHelper.d1Expect
  }

  def ee2 = {
    val parser = new CMParser(TestHelper.data2)
    parser.CM.run().get mustEqual TestHelper.d2Expect
  }

  def ee3 = {
    val parser = new CMParser(TestHelper.data3)
    parser.CM.run().get mustEqual TestHelper.d3Expect
  }

  def ee4 = {
    val parser = new CMParser(TestHelper.data4)
    parser.CM.run().get mustEqual TestHelper.d4Expect
  }


}
