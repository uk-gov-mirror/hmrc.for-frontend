
import net.ground5hark.sbt.concat.Import.*
import org.irundaia.sass.Minified
import play.core.PlayVersion
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, itSettings, scalaSettings}
import uk.gov.hmrc.SbtAutoBuildPlugin

val scoverageSettings = {
  Seq(
    // Semicolon-separated list of regex matching classes to exclude
    ScoverageKeys.coverageExcludedPackages :=
      """<empty>;uk\.gov\.hmrc\.BuildInfo;""" +
        """.*\.Routes;.*\.RoutesPrefix;.*Filters?;MicroserviceAuditConnector;Module;GraphiteStartUp;.*\.Reverse[^.]*;""" +
        """views\..*;.*\.template\.scala""",

    ScoverageKeys.coverageMinimumStmtTotal := 80.00,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true,
    Test / parallelExecution := false
  )
}

val bootstrapVersion = "8.2.0"
val playFrontendVersion = "8.2.0"
val mongoVersion = "1.6.0"

val compileDeps = Seq(
  filters,
  "uk.gov.hmrc" %% "bootstrap-frontend-play-30" % bootstrapVersion,
  "uk.gov.hmrc" %% "play-frontend-hmrc-play-30" % playFrontendVersion,
  "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30" % mongoVersion,
  "org.xhtmlrenderer" % "flying-saucer-pdf-itext5" % "9.1.22",
  "nu.validator" % "htmlparser" % "1.4.16",
  "org.webjars" % "jquery" % "3.7.1",
  "com.github.java-json-tools" % "json-schema-validator" % "2.2.14", // must be the same version as in "for-hod-adapter"
  "org.webjars.bower" % "compass-mixins" % "1.0.2"
)

val scalatestPlusPlayVersion = "7.0.0"
val scalatestVersion = "3.2.17"
val mockitoScalaVersion = "1.17.30"
val flexMarkVersion = "0.64.8"

val testDeps = Seq(
  "org.playframework" %% "play-test" % PlayVersion.current % Test,
  "org.scalatest" %% "scalatest" % scalatestVersion % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % Test,
  "org.mockito" %% "mockito-scala-scalatest" % mockitoScalaVersion % Test,
  "com.vladsch.flexmark" % "flexmark-all" % flexMarkVersion % Test // for scalatest 3.2.x
)

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always // Resolves versions conflict

ThisBuild / majorVersion := 3
ThisBuild / scalaVersion := "2.13.12"

lazy val microservice = Project("for-frontend", file("."))
  .enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(scalaSettings)
  .settings(defaultSettings())
  .settings(
    PlayKeys.playDefaultPort := 9521,
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    javaOptions += "-Xmx1G",
    libraryDependencies ++= compileDeps ++ testDeps,
    scoverageSettings,
    routesGenerator := InjectedRoutesGenerator,
  )
  .settings(
    SassKeys.generateSourceMaps := false,
    SassKeys.cssStyle := Minified
  )
  .settings(
    Concat.groups := Seq(
      "javascripts/app.js" -> group(Seq(
        "javascripts/application.js", "javascripts/common.js", "javascripts/feedback.js", "javascripts/intelAlerts.js",
        "javascripts/messages.js", "javascripts/radioToggle.js", "javascripts/voaFor.js"
      ))
    ),
    Assets / pipelineStages := Seq(concat, digest),
    // Include only final files for assets fingerprinting
    digest / includeFilter := GlobFilter("app.js") || GlobFilter("*.min.js") || GlobFilter("app.min.css")
  )

lazy val it =
  (project in file("it"))
    .enablePlugins(PlayScala)
    .dependsOn(microservice % "test->test")
    .settings(itSettings)
