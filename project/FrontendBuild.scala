import sbt._

object FrontendBuild extends Build with MicroService {

  val appName = "for-frontend"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()

  override val defaultPort: Int = 9521

  override lazy val playSettings: Seq[Setting[_]] = JavaScriptBuild.javaScriptUiSettings
}

private object AppDependencies {
  import play.sbt.PlayImport._
  import play.core.PlayVersion

  private val playHealthVersion = "2.0.0"
  private val logbackJsonLoggerVersion = "3.1.0"
  private val playUiVersion = "7.0.0"
  private val playPartialsVersion = "6.9.0-play-25"
  private val playAuthorisedFrontendVersion = "6.2.0"
  private val playConfigVersion = "3.0.0"
  private val hmrcTestVersion = "3.0.0"
  private val scalaTestVersion = "3.0.5"
  private val pegdownVersion = "1.6.0"
  private val scalatestPlusPlayVersion = "2.0.1"
  private val playUIVersion = "8.5.0-play-25"

  val compile = Seq(
    filters,
    ws,
    "uk.gov.hmrc" %% "json-encryption" % "4.4.0-play-25",
    "uk.gov.hmrc" %% "http-caching-client" % "9.0.0-play-25",
    "uk.gov.hmrc" %% "play-conditional-form-mapping" % "1.2.0-play-25",
    "uk.gov.hmrc" %% "http-verbs" % "10.2.0-play-25",
    "uk.gov.hmrc" %% "frontend-bootstrap" % "12.9.0",
    "uk.gov.hmrc" %% "play-partials" % playPartialsVersion,
    "uk.gov.hmrc" %% "play-ui" % playUIVersion,
    "uk.gov.hmrc" %% "url-builder" % "3.3.0-play-25",
    "it.innove" %  "play2-pdf" % "1.5.2",
    "joda-time" % "joda-time" % "2.8.2",
    "uk.gov.hmrc" %% "play-language" % "4.2.0-play-25",
    "uk.gov.hmrc" %% "mongo-caching" % "6.6.0-play-25",
    "uk.gov.hmrc" %% "simple-reactivemongo" % "7.22.0-play-25"
  )

  trait TestDependencies {
    lazy val scope: String = "test,it"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
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
