
import play.core.PlayVersion
import sbt.Keys.dependencyOverrides
import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import uk.gov.hmrc.{DefaultBuildSettings, SbtAutoBuildPlugin}
import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, defaultSettings, scalaSettings}

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := """<empty>;uk\.gov\.hmrc\.BuildInfo;""" +
     """.*\.Routes;.*\.RoutesPrefix;.*Filters?;MicroserviceAuditConnector;Module;GraphiteStartUp;.*\.Reverse[^.]*;""" +
    """views\..*;.*\.template\.scala""",

    ScoverageKeys.coverageMinimum := 80.00,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true,
    parallelExecution in Test := false
  )
}

lazy val compileDeps = Seq(
  filters,
  ws,
  "javax.inject" % "javax.inject" % "1",
  "uk.gov.hmrc" %% "json-encryption" % "4.8.0-play-26",
  "uk.gov.hmrc" %% "http-caching-client" % "9.1.0-play-26",
  "uk.gov.hmrc" %% "play-conditional-form-mapping" % "1.3.0-play-26",
  "uk.gov.hmrc" %% "http-verbs-play-26" % "11.7.0",
  "uk.gov.hmrc" %% "bootstrap-play-26" % "1.16.0",
  "uk.gov.hmrc" %% "play-partials" % "6.11.0-play-26",
  "uk.gov.hmrc" %% "play-ui" % "8.18.0-play-26",
  "uk.gov.hmrc" %% "url-builder" % "3.4.0-play-26",
  "uk.gov.hmrc" %% "play-frontend-govuk" % "0.56.0-play-26",
  "uk.gov.hmrc" %% "play-frontend-hmrc" % "0.29.0-play-26",
  "com.typesafe.play" %% "play-json-joda" % "2.6.14",
  "com.typesafe.play" %% "play-joda-forms" % PlayVersion.current,
  "uk.gov.hmrc" %% "play-language" % "4.4.0-play-26",
  "uk.gov.hmrc" %% "mongo-caching" % "6.15.0-play-26",
  "uk.gov.hmrc" %% "simple-reactivemongo" % "7.30.0-play-26",
  "org.xhtmlrenderer" % "flying-saucer-pdf-itext5" % "9.1.16",
  "nu.validator.htmlparser" % "htmlparser" % "1.4",
  "org.webjars.npm" % "govuk-frontend" % "3.8.1",
  "org.webjars.npm" % "hmrc-frontend" % "1.15.1",
  "org.webjars.bower" % "compass-mixins" % "0.12.7"
)

val scalatestPlusPlayVersion = "3.1.3"
val pegdownVersion = "1.6.0"

def testDeps(scope: String) = Seq(
  "org.pegdown" % "pegdown" % pegdownVersion % scope,
  "org.jsoup" % "jsoup" % "1.8.1" % scope,
  "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % scope,
  "org.mockito" %% "mockito-scala-scalatest" % "1.7.1" % scope
)
lazy val root = (project in file("."))
  .settings(scalaSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    name := "for-frontend",
    scalaVersion := "2.12.12",
    PlayKeys.playDefaultPort := 9521,
    javaOptions += "-Xmx1G",
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.bintrayRepo("hmrc", "release-candidates"),
      Resolver.typesafeRepo("releases"),
      Resolver.jcenterRepo
    ),
    libraryDependencies ++= compileDeps ++ testDeps("test") ++ testDeps("it"),
    publishingSettings,
    scoverageSettings,
    routesGenerator := InjectedRoutesGenerator,
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources",
    majorVersion := 3,
  ).settings(JavaScriptBuild.javaScriptUiSettings: _*)
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    DefaultBuildSettings.integrationTestSettings()
  )
  .enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .disablePlugins(plugins.JUnitXmlReportPlugin)