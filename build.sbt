
import play.core.PlayVersion
import sbt.Keys.dependencyOverrides
import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import uk.gov.hmrc.SbtAutoBuildPlugin

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
  "uk.gov.hmrc" %% "json-encryption" % "4.5.0-play-26",
  "uk.gov.hmrc" %% "http-caching-client" % "9.0.0-play-26",
  "uk.gov.hmrc" %% "play-conditional-form-mapping" % "1.2.0-play-26",
  "uk.gov.hmrc" %% "http-verbs" % "10.5.0-play-26",
  "uk.gov.hmrc" %% "bootstrap-play-26" % "1.3.0",
  "uk.gov.hmrc" %% "play-partials" % "6.9.0-play-26",
  "uk.gov.hmrc" %% "play-ui" % "8.8.0-play-26",
  "uk.gov.hmrc" %% "url-builder" % "3.3.0-play-26",
  "com.typesafe.play" %% "play-json-joda" % "2.6.14",
  "com.typesafe.play" %% "play-joda-forms" % PlayVersion.current,
  "uk.gov.hmrc" %% "play-language" % "4.2.0-play-26",
  "uk.gov.hmrc" %% "mongo-caching" % "6.8.0-play-26",
  "uk.gov.hmrc" %% "simple-reactivemongo" % "7.23.0-play-26",
  "org.xhtmlrenderer" % "flying-saucer-pdf-itext5" % "9.1.16",
  "nu.validator.htmlparser" % "htmlparser" % "1.4"
)

val akkaVersion     = "2.5.23"
val akkaHttpVersion = "10.0.15"
val scalatestPlusPlayVersion = "3.1.3"
val pegdownVersion = "1.6.0"

val dependencyOverride = Seq(
  "com.typesafe.akka" %% "akka-stream"    % akkaVersion     force(),
  "com.typesafe.akka" %% "akka-protobuf"  % akkaVersion     force(),
  "com.typesafe.akka" %% "akka-slf4j"     % akkaVersion     force(),
  "com.typesafe.akka" %% "akka-actor"     % akkaVersion     force(),
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion force()
)

def testDeps(scope: String) = Seq(
  "org.pegdown" % "pegdown" % pegdownVersion % scope,
  "org.jsoup" % "jsoup" % "1.8.1" % scope,
  "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % scope,
  "org.mockito" %% "mockito-scala-scalatest" % "1.7.1" % scope
)

lazy val root = (project in file("."))
  .settings(
    name := "for-frontend",
    organization := "uk.gov.hmrc",
    scalaVersion := "2.11.12",
    PlayKeys.playDefaultPort := 9521,
    javaOptions += "-Xmx1G",
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.bintrayRepo("hmrc", "release-candidates"),
      Resolver.typesafeRepo("releases"),
      Resolver.jcenterRepo
    ),
    libraryDependencies ++= compileDeps ++ testDeps("test") ++ testDeps("it"),
    dependencyOverrides ++= dependencyOverride,
    publishingSettings,
    scoverageSettings,
    fork in Test := true,
    routesGenerator := InjectedRoutesGenerator,
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources",
    majorVersion := 3
  ).settings(JavaScriptBuild.javaScriptUiSettings: _*)
  .configs(IntegrationTest)
  .settings(
    Keys.fork in IntegrationTest := false,
    Defaults.itSettings,
    unmanagedSourceDirectories in IntegrationTest += baseDirectory(_ / "it").value,
    parallelExecution in IntegrationTest := false,
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value)
  )
  .enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory)

def oneForkedJvmPerTest(tests: Seq[TestDefinition]) = {
  tests.map { test =>
    new Group(test.name, Seq(test), SubProcess(ForkOptions().withRunJVMOptions(Vector(s"-Dtest.name=${test.name}"))))
  }
}