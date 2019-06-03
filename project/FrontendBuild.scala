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
  private val playPartialsVersion = "6.3.0"
  private val playAuthorisedFrontendVersion = "6.2.0"
  private val playConfigVersion = "3.0.0"
  private val hmrcTestVersion = "3.0.0"
  private val scalaTestVersion = "3.0.5"
  private val pegdownVersion = "1.6.0"
  private val scalatestPlusPlayVersion = "2.0.1"
  private val playUIVersion = "7.27.0-play-25"

  val compile = Seq(
    filters,
    ws,
    "uk.gov.hmrc" %% "json-encryption" % "4.1.0",
    "uk.gov.hmrc" %% "http-caching-client" % "7.1.0" excludeAll (
      ExclusionRule("uk.gov.hmrc",  "http-core_2.11") // This library is deprecated - everything is in http-verbs
    ),
    "uk.gov.hmrc" %% "play-conditional-form-mapping" % "0.2.0",
    "uk.gov.hmrc" %% "http-verbs" % "8.10.0-play-25",
    "uk.gov.hmrc" %% "frontend-bootstrap" % "11.3.0",
    "uk.gov.hmrc" %% "play-partials" % playPartialsVersion,
    "uk.gov.hmrc" %% "play-ui" % playUIVersion,
    "uk.gov.hmrc" %% "url-builder" % "2.1.0",
    "it.innove" %  "play2-pdf" % "1.5.2",
    "joda-time" % "joda-time" % "2.8.2",
    "uk.gov.hmrc" %% "play-language" % "3.4.0",
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
        "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % scope
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
