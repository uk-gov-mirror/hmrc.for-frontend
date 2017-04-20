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
  private val frontendBootstrapVersion = "7.19.0"
  private val govukTemplateVersion = "5.0.0"
  private val playUiVersion = "7.0.0"
  private val playPartialsVersion = "5.2.0"
  private val playAuthorisedFrontendVersion = "6.2.0"
  private val playConfigVersion = "3.0.0"
  private val hmrcTestVersion = "2.1.0"
  private val scalaTestVersion = "3.0.1"
  private val pegdownVersion = "1.6.0"

  val compile = Seq(
    filters,
    ws,
    "uk.gov.hmrc" %% "json-encryption" % "3.1.0",
    "uk.gov.hmrc" %% "http-caching-client" % "6.1.0",
    "uk.gov.hmrc" %% "frontend-bootstrap" % frontendBootstrapVersion,
    "uk.gov.hmrc" %% "play-partials" % playPartialsVersion,
    "uk.gov.hmrc" %% "play-config" % playConfigVersion,
    "uk.gov.hmrc" %% "logback-json-logger" % logbackJsonLoggerVersion,
    "uk.gov.hmrc" %% "govuk-template" % govukTemplateVersion,
    "uk.gov.hmrc" %% "play-health" % playHealthVersion,
    "uk.gov.hmrc" %% "play-ui" % playUiVersion,
    "uk.gov.hmrc" %% "url-builder" % "2.0.0",
    "it.innove" %  "play2-pdf" % "1.5.2",
    "joda-time" % "joda-time" % "2.8.2",
    "uk.gov.hmrc" %% "play-language" % "3.0.0",
    "uk.gov.hmrc" %% "play-conditional-form-mapping" % "0.2.0"
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
        "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % scope
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
        "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % scope
      )
    }.test
  }

  def apply() = compile ++ Test() ++ IntegrationTest()

}
