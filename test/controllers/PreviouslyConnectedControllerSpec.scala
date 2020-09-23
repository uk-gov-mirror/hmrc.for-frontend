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

import form.persistence.{FormDocumentRepository, MongoSessionRepository}
import org.mockito.scalatest.MockitoSugar
import org.scalatest.{AsyncFlatSpec, FlatSpec, Matchers}
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import uk.gov.hmrc.http.cache.client.ShortLivedCache
import play.api.test.Helpers._
import views.html.previouslyConnected

import scala.concurrent.ExecutionContext.Implicits.global

class PreviouslyConnectedControllerSpec extends FlatSpec with Matchers with MockitoSugar {



  //TODO Unable to test. Lot of static dependencies.
  "Controller" should "redirect after form submission" ignore {

    implicit val messagesApi = mock[MessagesApi]

    val cache = mock[MongoSessionRepository]

    val formDocumentRepository = mock[FormDocumentRepository]

    val controller = new PreviouslyConnectedController(???, cache, formDocumentRepository, ???, mock[previouslyConnected])(???)

    val request = FakeRequest("POST", "/path").withSession("refNum" ->"11122")

    val response = controller.onPageSubmit().apply(request)

    status(response) shouldBe(303)

    header("location", response).value shouldBe "/sending-rental-information/page/1"



  }



}
