/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import form.persistence.FormDocumentRepository
import helpers.AddressAuditing
import models.serviceContracts.submissions.Submission
import org.scalatest.{FreeSpec, Matchers, MustMatchers}
import org.scalatestplus.play.guice.{GuiceFakeApplicationFactory, GuiceOneAppPerTest, GuiceOneServerPerSuite}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Controller
import play.api.test.FakeRequest
import play.api.test.Helpers._
import useCases.SubmitBusinessRentalInformation
import utils.stubs.{StubAddressAuditing, StubFormDocumentRepo}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import uk.gov.hmrc.http.{ HeaderCarrier, HeaderNames }

class FORSubmissionControllerSpec extends FreeSpec with Matchers with GuiceOneAppPerTest with GuiceFakeApplicationFactory {

  import TestData._

  override def fakeApplication() = new GuiceApplicationBuilder()
    .configure(Map("auditing.enabled" -> false)).build()

  "When a submission is received and the declaration has been agreed to" - {
    val submit = StubSubmitBRI()
    val controller = createController(submit)

    "A 302 response redirecting to the confirmation page is returned" in {
      val request = FakeRequest().withSession("refNum" -> refNum).withFormUrlEncodedBody("declaration" -> "true").withHeaders(HeaderNames.xSessionId -> sessionId)
      val response = Await.result(controller.submit()(request), 5 seconds)

      response.header.status should equal(302)
      assert(response.header.headers("Location") === confirmationUrl)
    }

    "The Business rental information submission process is initiated" in {
      submit.assertBRISubmittedFor(refNum)
    }
  }

  "When a submission is received and the declaration has not been agreed to" - {
    val controller = createController()

    "A redirect to the declaration error page is returned" in {
      val request = FakeRequest().withSession(("refNum" -> refNum)).withFormUrlEncodedBody(("declaration" -> "false"))
      val response = Await.result(controller.submit()(request), 5 seconds)

      response.header.status should equal(302)
      assert(response.header.headers("Location") === declarationErrorUrl)
    }
  }

  object TestData {
    lazy val refNum = "adfiwerq08342kfad"
    lazy val sessionId = "sessionid"

    lazy val confirmationUrl = controllers.feedback.routes.Survey.confirmation.url

    lazy val declarationErrorUrl = controllers.routes.Application.declarationError.url

    class TestController(val x: SubmitBusinessRentalInformation) extends FORSubmissionController with Controller {
      override protected val documentRepo: FormDocumentRepository = StubFormDocumentRepo()
      override protected val auditAddresses: AddressAuditing = StubAddressAuditing

      def submitBusinessRentalInformation = x
    }

    def createController(submitter: StubSubmitBRI = null) = {
      new TestController(submitter)
    }
  }

}

object StubSubmitBRI {
  def apply() = new StubSubmitBRI
}

class StubSubmitBRI extends SubmitBusinessRentalInformation with MustMatchers {
  lazy val stubSubmission = Submission(
    None, None, None, None, None, None, None, None, None, None, None, None, None, None, None
  )

  var submittedRefNums: Seq[String] = Seq.empty

  def apply(refNum: String)(implicit hc: HeaderCarrier): Future[Submission] = {
    Future.successful {
      submittedRefNums = submittedRefNums :+ refNum;
      stubSubmission
    }
  }

  def assertBRISubmittedFor(refNum: String) {
    submittedRefNums must equal(Seq(refNum))
  }
}
