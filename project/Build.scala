import sbt._
import Keys._
import Deps._
import sbtassembly.Plugin.AssemblyKeys._
import sbtassembly.Plugin._


object Kunfugirator extends Build {

  private def module (modName: String)(prjId: String = modName, dirName: String = modName, sets: Seq[Setting[_]] = Nil, deps: Seq[sbt.ModuleID] = Deps.common) =
    Project(id = prjId, base = file(dirName),
      settings = ( Defaults.defaultSettings ++ testSettings ++ commonSettings ++ depGraph ++ sets ++ Seq(libraryDependencies ++= deps)))


  // modules definition
  lazy val all = Project(
    id = "conf4s",
    base = file("."),
    aggregate = Seq(core),
    settings = Defaults.defaultSettings ++ commonSettings ++ Seq(
      moduleName := "conf4s-all",

      (unmanagedSourceDirectories in Compile) := Nil,
      (unmanagedSourceDirectories in Test) := Nil,

      publish := (),
      publishLocal := ()
    )
  )

  lazy val core = module("core")(deps = Deps.coreProject, sets = coreSettings)

  //settings
  def commonSettings: Seq[Setting[_]] = Seq(
      organization          := "com.github.siskape",
      version               := "0.0.1-SNAPSHOT",
      scalaVersion          := "2.11.0",
      scalacOptions ++= Seq("-Xcheckinit", "-encoding", "utf8", "-deprecation", "-unchecked", "-feature", "-Xlint",
        "-language:postfixOps", "-language:higherKinds", "-language:implicitConversions"),
      (unmanagedSourceDirectories in Compile) <<= (scalaSource in Compile)(Seq(_)),
      (unmanagedSourceDirectories in Test) <<= (scalaSource in Test)(Seq(_)),
      resourceDirectory <<= baseDirectory { _ / "src" },
      parallelExecution in Test := false,
      crossVersion              := CrossVersion.full,
      resolvers ++= Deps.resolutionRepos
  )

  def coreSettings: Seq[Setting[_]] = Seq(
      initialCommands in console := s"""
      import scala.collection.convert.decorateAll._
      """,
      initialCommands in consoleQuick := """import scala.collection.convert.decorateAll._"""
  )

  def testSettings = Seq(
    testFrameworks ++= Seq(TestFrameworks.Specs2, new TestFramework("org.scalameter.ScalaMeterFramework")),
    testOptions := Seq(Tests.Filter(s => Seq("Spec", "Unit", "Suite", "Test", "Benchmark").exists(s.endsWith(_)))),
    testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "console", "junitxml")
  )

  def depGraph = Seq(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
}

object Format {

  import com.typesafe.sbt.SbtScalariform._

  lazy val settings = scalariformSettings ++ Seq(
    ScalariformKeys.preferences := formattingPreferences
  )

  lazy val formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences().
      setPreference(AlignParameters, true).
      setPreference(AlignSingleLineCaseStatements, true).
      setPreference(CompactControlReadability, false).
      setPreference(CompactStringConcatenation, false).
      setPreference(DoubleIndentClassDeclaration, false).
      setPreference(FormatXml, true).
      setPreference(IndentLocalDefs, true).
      setPreference(IndentPackageBlocks, true).
      setPreference(IndentSpaces, 2).
      setPreference(MultilineScaladocCommentsStartOnFirstLine, false).
      setPreference(PreserveSpaceBeforeArguments, false).
      setPreference(PreserveDanglingCloseParenthesis, false).
      setPreference(RewriteArrowSymbols, false).
      setPreference(SpaceBeforeColon, false).
      setPreference(SpaceInsideBrackets, false).
      setPreference(SpacesWithinPatternBinders, true)
  }
}
