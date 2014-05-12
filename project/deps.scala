import sbt._
import Keys._

object Deps {
  val resolutionRepos = Seq(
    "apache.releases"   at "https://repository.apache.org/content/repositories/releases/",
    "apache.snap"       at "https://repository.apache.org/content/repositories/snapshots/",
    "sonatype.repo"     at "https://oss.sonatype.org/content/repositories/public",
    "sonatype.releases" at "http://oss.sonatype.org/content/repositories/releases",
    "sonatype.snap"     at "http://oss.sonatype.org/content/repositories/snapshots",
    "typesafe.releases" at "http://repo.typesafe.com/typesafe/releases/",
    "typesafe.snap"     at "http://repo.typesafe.com/typesafe/snapshots/",
    "java.net.m2.repo"  at "http://download.java.net/maven/2/",
    "softprops-maven"   at "http://dl.bintray.com/content/softprops/maven",
    "twitter.repo"      at "http://maven.twttr.com/",
    "novus.releases"    at "http://repo.novus.com/releases/",
    "novus.snaps"       at "http://repo.novus.com/snapshots/",
    "spray repo"        at "http://repo.spray.io/",
    "mth.io.snaps"      at "http://repo.mth.io/snapshots",
    "mth.io.releases"   at "http://repo.mth.io/releases"
  )

  lazy val coreProject = common ++ Seq(parboiled2)
  lazy val common      = scalazGroup ++ utilsGroup ++ testGroup

  val parboiled2       = "org.parboiled" %% "parboiled" % "2.0.0-RC1"

  private val scalazVersion = "7.0.6"
  lazy val scalazGroup = Seq(sz_core, sz_effect, sz_concurrent, sz_typelevel)
  val sz_core          = "org.scalaz" %% "scalaz-core" % scalazVersion
  val sz_effect        = "org.scalaz" %% "scalaz-effect" % scalazVersion
  val sz_concurrent    = "org.scalaz" %% "scalaz-concurrent" % scalazVersion
  val sz_typelevel     = "org.scalaz" %% "scalaz-typelevel" % scalazVersion

  lazy val utilsGroup  = Seq(ntime)
  val ntime            = "com.github.nscala-time" %% "nscala-time" % "1.0.0"

  lazy val testGroup   = Seq(scalacheck, specs2, scalaMeter)
  val scalacheck       = "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
  val specs2           = "org.specs2" %% "specs2" % "2.3.11" % "test"
  val scalaMeter       = "com.github.axel22" %% "scalameter" % "0.5-M2"

}

