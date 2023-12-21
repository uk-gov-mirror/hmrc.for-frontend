import org.irundaia.sass.Minified

import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, itSettings, scalaSettings}
import uk.gov.hmrc.SbtAutoBuildPlugin

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always // Resolves versions conflict

ThisBuild / majorVersion := 3
ThisBuild / scalaVersion := "2.13.12"
ThisBuild / scalafixScalaBinaryVersion := "2.13"

lazy val microservice = Project("for-frontend", file("."))
  .enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(scalaSettings)
  .settings(defaultSettings())
  .settings(CodeCoverageSettings.settings)
  .settings(
    PlayKeys.playDefaultPort := 9521,
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    javaOptions += "-Xmx1G",
    libraryDependencies ++= AppDependencies.appDependencies,
    routesGenerator := InjectedRoutesGenerator
  )
  .settings(
    SassKeys.generateSourceMaps := false,
    SassKeys.cssStyle := Minified
  )
  .settings(
    Concat.groups := Seq(
      "javascripts/app.js" -> group(Seq(
        "javascripts/application.js",
        "javascripts/common.js",
        "javascripts/feedback.js",
        "javascripts/intelAlerts.js",
        "javascripts/messages.js",
        "javascripts/radioToggle.js",
        "javascripts/voaFor.js"
      ))
    ),
    Assets / pipelineStages := Seq(concat, digest),
    // Include only final files for assets fingerprinting
    digest / includeFilter := GlobFilter("app.js") || GlobFilter("*.min.js") || GlobFilter("app.min.css")
  )
  .settings(
    scalafmtFailOnErrors := true,
    // Test / test := ((Test / test) dependsOn formatAll).value,
    formatAll := Def
      .sequential(
        scalafmtAll,
        Compile / scalafmtSbt,
        scalafixAll.toTask(""),
        (Compile / scalastyle).toTask("")
      )
      .value
  )
  .settings( // sbt-scalafix
    semanticdbEnabled := true, // enable SemanticDB
    semanticdbVersion := scalafixSemanticdb.revision, // only required for Scala 2.x
    scalacOptions += "-Ywarn-unused" // Scala 2.x only, required by `RemoveUnused`
  )

lazy val formatAll: TaskKey[Unit] = taskKey[Unit]("Run scalafmt for all files")

lazy val it =
  (project in file("it"))
    .enablePlugins(PlayScala)
    .dependsOn(microservice % "test->test")
    .settings(itSettings)
