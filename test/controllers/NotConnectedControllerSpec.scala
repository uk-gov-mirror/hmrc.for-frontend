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

import connectors.{Audit, SubmissionConnector}
import form.persistence.{FormDocumentRepository, MongoSessionRepository}
import org.mockito.scalatest.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.Helpers._

import scala.concurrent.ExecutionContext

class NotConnectedControllerSpec extends FlatSpec with Matchers with MockitoSugar {

  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

  "NotConnectedController" should "Audit submission" in {
    val configration = Configuration()

    val submissionConnector = mock[SubmissionConnector]
    val cache = mock[MongoSessionRepository]
    val audit = mock[Audit]
    val formDocumentRepository = mock[FormDocumentRepository]

    implicit val messageApi = mock[MessagesApi]

    val controller = new NotConnectedController(configration, formDocumentRepository, submissionConnector,
      refNumAction(), cache, audit, stubMessagesControllerComponents())

    val fakeRequest = FakeRequest()

    val result = controller.onPageSubmit().apply(fakeRequest)

    status(result) shouldBe(SEE_OTHER)


  }


}
