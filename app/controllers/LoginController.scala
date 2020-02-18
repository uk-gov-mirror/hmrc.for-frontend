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

import actions.RefNumAction
import connectors.{Audit, HODConnector}
import form.ConditionalMapping._
import form.Errors
import form.MappingSupport._
import javax.inject.Inject
import org.joda.time.DateTime
import play.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.JodaForms._
import play.api.libs.json.{Format, Json}
import play.api.mvc.{AnyContent, MessagesControllerComponents, Request, Result}
import playconfig.LoginToHOD
import security.{DocumentPreviouslySaved, NoExistingDocument}
import uk.gov.hmrc.http.logging.SessionId
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys, Upstream4xxResponse}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

case class LoginDetails(ref1: String, ref2: String, postcode: String, startTime: DateTime)



class LoginController @Inject()(audit: Audit, hodConnector: HODConnector, cc: MessagesControllerComponents
                       )(implicit ec: ExecutionContext) extends FrontendController(cc) {

  val loginForm = Form(
    mapping(
      "ref1" -> text.verifying(Errors.invalidRefNum, x => Seq(7, 8).contains(x.length)),
      "ref2" -> text.verifying(Errors.invalidRefNum, _.length == 3),
      "postcode" -> nonEmptyTextOr("postcode", postcode),
      "start-time" -> jodaDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    )(LoginDetails.apply)(LoginDetails.unapply))

  def show = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def logout = Action { implicit request =>
    val refNum = request.session.get("refNum").getOrElse("-")
    audit.sendExplicitAudit("Logout", Json.obj(Audit.referenceNumber -> refNum))
    Redirect(routes.LoginController.show()).withNewSession
  }

  def submit = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.login(formWithErrors))),
      loginData => verifyLogin(loginData.ref1, loginData.ref2, loginData.postcode, loginData.startTime)
    )
  }

  def verifyLogin(ref1: String, ref2: String, postcode: String, startTime: DateTime)(implicit r: Request[AnyContent]) = {
    val sessionId = java.util.UUID.randomUUID().toString //TODO - Why new session? Why manually?

    implicit val hc2: HeaderCarrier = hc.copy(sessionId = Some(SessionId(sessionId)))

    //TODO - refactor
    LoginToHOD(hc2)(ref1, ref2, postcode, startTime).flatMap {
      case DocumentPreviouslySaved(doc, token) =>
        auditLogin(ref1 + ref2, true, hc2)
        withNewSession(Redirect(routes.SaveForLater.resumeOptions()), token, s"$ref1$ref2", sessionId)
      case NoExistingDocument(token) =>
        auditLogin(ref1 + ref2, false, hc2)
        withNewSession(Redirect(dataCapturePages.routes.PageController.showPage(0)), token, s"$ref1$ref2", sessionId)
    }.recover {
      case Upstream4xxResponse(_, 409, _, _) => Conflict(views.html.error.error409())
      case Upstream4xxResponse(_, 403, _, _) => Redirect(routes.Application.fail())
      case Upstream4xxResponse(body, 401, _, _) =>
        val failed = Json.parse(body).as[FailedLoginResponse]
        Logger.info(s"Failed login: RefNum: $ref1$ref2 Attempts remaining: ${failed.numberOfRemainingTriesUntilIPLockout}")
        if (failed.numberOfRemainingTriesUntilIPLockout < 1)
          Redirect(routes.LoginController.lockedOut)
        else
          Redirect(routes.LoginController.loginFailed(failed.numberOfRemainingTriesUntilIPLockout))
    }
  }

  private def auditLogin(refNumber: String, returnUser: Boolean, hc: HeaderCarrier) = {
    audit.sendExplicitAudit("UserLogin",Json.obj("returningUser" -> returnUser, Audit.referenceNumber -> refNumber))(hc, playExecutionContext())
  }

  def lockedOut = Action { implicit request => Unauthorized(views.html.lockedOut()) }

  def loginFailed(attemptsRemaining: Int) = Action { implicit request => Unauthorized(views.html.loginFailed(attemptsRemaining)) }

  private def withNewSession(r: Result, token: String, ref: String, sessionId: String)(implicit req: Request[AnyContent]) = {
    r.withSession(
      (req.session.data ++ Seq(SessionKeys.sessionId -> sessionId, SessionKeys.authToken -> token, "refNum" -> ref)).toSeq: _*
    )
  }
}

object FailedLoginResponse {
  implicit val f: Format[FailedLoginResponse] = Json.format[FailedLoginResponse]
}

case class FailedLoginResponse(numberOfRemainingTriesUntilIPLockout: Int)
