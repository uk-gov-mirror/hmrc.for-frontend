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

import connectors.{Audit, HODConnector}
import form.persistence.FormDocumentRepository
import models.FORLoginResponse
import models.serviceContracts.submissions.Address
import org.joda.time.DateTime
import org.mockito.scalatest.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsObject, Json}
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers._
import playconfig.LoginToHODAction
import security.LoginToHOD.{Postcode, Ref1, Ref2, StartTime}
import utils.Helpers._
import security.{LoginResult, NoExistingDocument}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.{login, loginFailed}

import scala.concurrent.{ExecutionContext, Future}

class LoginControllerSpec extends FlatSpec with Matchers with MockitoSugar {
  implicit val globalEc = scala.concurrent.ExecutionContext.Implicits.global

  "login controller" should "Audit successful login" in {

    val audit = mock[Audit]
    doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

    val loginToHodFunction = (ref1: Ref1, ref2: Ref2, postcode: Postcode, start:StartTime) =>{
      assert(ref1.equals("01234567"))
      assert(ref2.equals("000"))
      Future.successful(NoExistingDocument("token"))
    }

    val loginToHod = mock[LoginToHODAction]
    val time = DateTime.now()
    when(loginToHod.apply(any[HeaderCarrier])).thenReturn(loginToHodFunction)

    val loginController:LoginController = new LoginController(audit, loginToHod, stubMessagesControllerComponents(), mock[login], mock[loginFailed])

    val fakeRequest = FakeRequest()
    //should strip out all non digits then split string 3 from end to create ref1/ref2
    val response = loginController.verifyLogin("01234567/*ok blah 000", "BN12 1AB", time)(fakeRequest)

    status(response) shouldBe(SEE_OTHER)

    verify(audit).sendExplicitAudit(eqTo("UserLogin"), eqTo(Json.obj(Audit.referenceNumber -> "01234567000", "returningUser" -> false))
    )(any[HeaderCarrier], any[ExecutionContext])

  }

  "Login Controller" should "Audit logout event" in {
    val audit = mock[Audit]
    doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

    val loginController = new LoginController(audit, null, stubMessagesControllerComponents(), mock[login], mock[loginFailed])

    val fakeRequest = FakeRequest()

    val response = loginController.logout().apply(fakeRequest)

    status(response) shouldBe(SEE_OTHER)

    verify(audit).sendExplicitAudit(eqTo("Logout"), eqTo(Json.obj(Audit.referenceNumber -> "-"))
    )(any[HeaderCarrier], any[ExecutionContext])

  }

  def loginToHod(ref1: String, ref2: String, postcode: String, start: DateTime): Future[LoginResult] = {
    Future.successful(NoExistingDocument("secureKey"))
  }

}
