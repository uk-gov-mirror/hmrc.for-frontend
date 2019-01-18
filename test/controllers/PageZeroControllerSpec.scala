/*
 * Copyright 2019 HM Revenue & Customs
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

import connectors.Document
import controllers.dataCapturePages.PageZeroController
import form.persistence.{FormDocumentRepository, SaveForm, SaveFormInRepository}
import models.pages.SummaryBuilder
import org.joda.time.DateTime
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import org.scalatestplus.play.OneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}
import play.filters.csrf.CSRF.Token
import play.filters.csrf._
import utils.stubs.StubFormDocumentRepo
import uk.gov.hmrc.http.HeaderNames

class PageZeroControllerSpec extends FreeSpec with MustMatchers with FutureAwaits with DefaultAwaitTimeout with OptionValues with OneServerPerSuite {
  implicit override lazy val app: play.api.Application = new GuiceApplicationBuilder().configure(Map("auditing.enabled" -> false)).build()

  val testRefNum = "1234567890"
  val sessionId = java.util.UUID.randomUUID().toString

  def addToken[T](fakeRequest: FakeRequest[T])(implicit app: play.api.Application) = {
    val csrfConfig     = app.injector.instanceOf[CSRFConfigProvider].get
    val csrfFilter     = app.injector.instanceOf[CSRFFilter]
    val token          = csrfFilter.tokenProvider.generateToken

    fakeRequest.copyFakeRequest(tags = fakeRequest.tags ++ Map(
      Token.NameRequestTag  -> csrfConfig.tokenName,
      Token.RequestTag      -> token
    )).withHeaders((csrfConfig.headerName, token))
  }

  private object TestPageZeroController extends PageZeroController {
    override def saveForm: SaveForm = new SaveFormInRepository(repository, SummaryBuilder)
    override def repository: FormDocumentRepository = StubFormDocumentRepo((sessionId, testRefNum, Document(testRefNum, DateTime.now())))
  }

  "When the user still has a relationship with the property" - {
    "Then they are directed to page one of the form" in {
      running(app) {
        val request = addToken(FakeRequest()
          .withHeaders(HeaderNames.xSessionId -> sessionId)
          .withSession("refNum" -> testRefNum)
          .withFormUrlEncodedBody(
            "isRelated" -> "true",
            "continue_button" -> ""
          ))

        val res = await(TestPageZeroController.save()(request))

        status(res) mustBe SEE_OTHER
        header("location", res).value mustBe "/sending-rental-information/page/1"
      }
    }
  }

  "When the user no longer has a relationship with the property" - {
    "Then they are redirected to a mailto link" in {
      running(app) {
        val request = addToken(FakeRequest()
          .withHeaders(HeaderNames.xSessionId -> sessionId)
          .withSession("refNum" -> testRefNum)
          .withFormUrlEncodedBody(
            "isRelated" -> "false",
            "continue_button" -> ""
          ))

        val res = await(TestPageZeroController.save()(request))

        status(res) mustBe SEE_OTHER
        header("location", res).value mustBe "/sending-rental-information/inpage-vacated-form"
      }
    }
  }
}
