package aat

import com.typesafe.config.Config
import config.ForGlobal
import helpers.{AppNameHelper, RunModeHelper}
import models.FORLoginResponse
import models.serviceContracts.submissions.Address
import org.scalatest.{BeforeAndAfterAll, FreeSpec, FreeSpecLike, Matchers}
import org.scalatestplus.play.guice._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json, Writes}
import playconfig.ForHttp
import uk.gov.hmrc.http.{HeaderCarrier, _}

import scala.concurrent.Future

trait AcceptanceTest extends FreeSpec with Matchers with GuiceOneServerPerSuite {
  private lazy val testConfigs = Map("auditing.enabled" -> false, "agentApi.testAccountsOnly" -> true)

  lazy val http: TestHttpClient = new TestHttpClient()

  val global = new ForGlobal {
    override lazy val forHttp = http
  }

  override lazy val port = 9521

  override def fakeApplication() = new GuiceApplicationBuilder().global(global).configure(testConfigs).build()
}

class TestHttpClient extends ForHttp with RunModeHelper with AppNameHelper {
  import views.html.helper.urlEncode

  override protected def configuration: Option[Config] = Option(runModeConfiguration.underlying)

  private val baseForUrl = "http://localhost:9522/for"
  type Headers = Seq[(String, String)]

  private var stubbedGets: Seq[(String, Headers, HttpResponse)] = Nil // scalastyle:ignore
  private var stubbedPuts: Seq[(String, Any, Headers, HttpResponse)] = Nil // scalastyle:ignore

  def stubGet(url: String, headers: Seq[(String, String)], response: HttpResponse) = {
    stubbedGets :+= (url, headers, response)
  }

  def stubPut[A](url: String, body: A, headers: Seq[(String, String)], response: HttpResponse) = {
    stubbedPuts :+= (url, body, headers, response)
  }

  def stubValidCredentials(ref1: String, ref2: String, postcode: String) = {
    stubGet(s"$baseForUrl/$ref1/$ref2/${urlEncode(postcode)}/verify", Nil, HttpResponse(
      responseStatus = 200,
      responseJson = Some(Json.toJson(FORLoginResponse("token", Address("1", None, None, "AA11 1AA"))))
    ))
  }

  def stubInvalidCredentials(ref1: String, ref2: String, postcode: String) = {
    stubGet(s"$baseForUrl/$ref1/$ref2/${urlEncode(postcode)}/verify", Nil, HttpResponse(
      responseStatus = 401,
      responseJson = Some(Json.parse("""{"numberOfRemainingTriesUntilIPLockout":4}"""))))
  }

  def stubConflictingCredentials(ref1: String, ref2: String, postcode: String) = {
    stubGet(s"$baseForUrl/$ref1/$ref2/${urlEncode(postcode)}/verify", Nil, HttpResponse(
      responseStatus = 409,
      responseJson = Some(Json.parse("{\"error\":\"Duplicate submission. 1234567890\"}"))))
  }

  def stubIPLockout(ref1: String, ref2: String, postcode: String) = {
    stubGet(s"$baseForUrl/$ref1/$ref2/${urlEncode(postcode)}/verify", Nil, HttpResponse(
      responseStatus = 401,
      responseJson = Some(Json.parse("""{"numberOfRemainingTriesUntilIPLockout":0}"""))))
  }

  def stubInternalServerError(ref1: String, ref2: String, postcode: String) = {
    stubGet(s"$baseForUrl/$ref1/$ref2/${urlEncode(postcode)}/verify", Nil, HttpResponse(
      responseStatus = 500
    ))
  }

  def stubSubmission(refNum: String, submission: JsValue, headers: Seq[(String, String)], response: HttpResponse) = {
    stubPut(s"$baseForUrl/submissions/$refNum", submission, headers, response)
  }

  override protected def get(url: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    stubbedGets.find(x => x._1 == url && x._2.forall(y => hc.headers.exists(h => h._1 == y._1 && h._2 == y._2))) match {
      case Some((_, _, res)) => Future.successful(res)
      case _ => throw new HttpRequestNotStubbed(url, hc)
    }
  }

  override protected def put[A](url: String, body: A)(implicit rds: Writes[A], hc: HeaderCarrier): Future[HttpResponse] = {
    stubbedPuts.find(x => x._1 == url && x._2 == body && x._3.forall(y => hc.headers.exists(h => h._1 == y._1 && h._2 == y._2))) match {
      case Some((_, _, _, res)) => Future.successful(res)
      case _ => throw new HttpRequestNotStubbed(url, hc)
    }
  }
}

class HttpRequestNotStubbed[A](url: String, hc: HeaderCarrier, data: Option[A] = None)
  extends Exception(s"Request not stubbed: $url - ${hc.headers} ${data.map { d => s"- $d" }.getOrElse("")}")