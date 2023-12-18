
import org.irundaia.sass.Minified
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
    libraryDependencies ++= AppDependencies.appDependencies,
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
