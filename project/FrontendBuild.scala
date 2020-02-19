import sbt._

object FrontendBuild extends Build with MicroService {

  val appName = "for-frontend"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()

  override lazy val dependencyOverride: Set[ModuleID] = AppDependencies.dependencyOverrides

  override val defaultPort: Int = 9521

  override lazy val playSettings: Seq[Setting[_]] = JavaScriptBuild.javaScriptUiSettings
}

private object AppDependencies {
  import play.sbt.PlayImport._
  import play.core.PlayVersion

  private val hmrcTestVersion = "3.0.0"
  private val scalaTestVersion = "3.0.5"
  private val pegdownVersion = "1.6.0"
  private val scalatestPlusPlayVersion = "2.0.1"

  private val akkaVersion     = "2.5.23"
  private val akkaHttpVersion = "10.0.15"

  val compile = Seq(
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
    "it.innove" %  "play2-pdf" % "1.5.2",
    //"joda-time" % "joda-time" % "2.8.2",
    "uk.gov.hmrc" %% "play-language" % "4.2.0-play-26",
    "uk.gov.hmrc" %% "mongo-caching" % "6.8.0-play-26",
    "uk.gov.hmrc" %% "simple-reactivemongo" % "7.23.0-play-26"
  )

  val dependencyOverrides = Set(
    "com.typesafe.akka" %% "akka-stream"    % akkaVersion     force(),
    "com.typesafe.akka" %% "akka-protobuf"  % akkaVersion     force(),
    "com.typesafe.akka" %% "akka-slf4j"     % akkaVersion     force(),
    "com.typesafe.akka" %% "akka-actor"     % akkaVersion     force(),
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion force()
  )

  trait TestDependencies {
    lazy val scope: String = "test,it"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % "3.9.0-play-26" % scope,
        "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "org.jsoup" % "jsoup" % "1.8.1" % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % scope,
        "org.mockito" %% "mockito-scala-scalatest" % "1.5.16" % scope
      )
    }.test
  }

  object IntegrationTest {
    def apply() = new TestDependencies {
      override lazy val scope = "it"

      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "org.jsoup" % "jsoup" % "1.8.1" % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % scope
      )
    }.test
  }

  def apply() = compile ++ Test() ++ IntegrationTest()

}
