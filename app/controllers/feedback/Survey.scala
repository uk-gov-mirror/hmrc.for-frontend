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

package controllers.feedback

import actions.RefNumAction
import controllers._
import form.Formats._
import form.persistence.FormDocumentRepository
import models.pages.SummaryBuilder
import models.{Journey, NormalJourney, PdfSize, Satisfaction}
import play.api.data.Forms._
import play.api.data.{Form, Forms}
import play.api.mvc.{Action, Request, RequestHeader}
import playconfig.{Audit, FormPersistence, SessionId}
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import uk.gov.hmrc.http.HeaderCarrier

object Survey extends PostSubmitFeedback {
  val repository = FormPersistence.formDocumentRepository
}

trait PostSubmitFeedback extends FrontendController {
  implicit val ec: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def repository: FormDocumentRepository

  val completedFeedbackForm = Form(mapping(
    "satisfaction" -> Forms.of[Satisfaction],
    "details" -> text(maxLength = 1200),
    "journey" -> Forms.of[Journey]
  )(SurveyFeedback.apply)(SurveyFeedback.unapply))

  val completedFeedbackFormNormalJourney = completedFeedbackForm.bind(Map("journey" -> NormalJourney.name)).discardingErrors

  def confirmation = RefNumAction.async { implicit request =>
    viewConfirmationPage(request.refNum)
  }

  def formCompleteFeedback = RefNumAction.async { implicit request =>
    completedFeedbackForm.bindFromRequest.fold(
      formWithErrors => viewConfirmationPage(request.refNum, Some(formWithErrors)),
      success => {
        sendFeedback(success, request.refNum) map { _ => Redirect(routes.Survey.surveyThankyou()) }
      }
    )
  }

  def inpageAfterSubmissionFeedbackForm  = RefNumAction { implicit request =>
    Ok(views.html.inpageAfterSubmissionFeedbackForm(completedFeedbackFormNormalJourney))
  }

  private def host(implicit request: RequestHeader): String = {
    s"http://${request.host}/"
  }

  private def viewConfirmationPage(refNum: String, form: Option[Form[SurveyFeedback]] = None)(implicit rh: RequestHeader, hc: HeaderCarrier) =
    repository.findById(SessionId(hc), refNum) map {
      case Some(doc) =>
        val summary = SummaryBuilder.build(doc)
        Ok(views.html.confirm(
          form getOrElse completedFeedbackFormNormalJourney, refNum,
          summary.customerDetails.map(_.contactDetails.email).getOrElse(""), summary))
      case None => InternalServerError(views.html.error.error500())
    }

  private def sendFeedback(f: SurveyFeedback, refNum: String)(implicit request: Request[_]) = {
    Audit("SurveySatisfaction", Map("satisfaction" -> f.satisfaction.rating.toString, "referenceNumber" -> refNum, "journey" -> f.journey.name)).flatMap { _ =>
      Audit("SurveyFeedback", Map("feedback" -> f.details, "referenceNumber" -> refNum, "journey" -> f.journey.name))
    }
  }

  def surveyThankyou = Action { implicit request =>
    Ok(views.html.surveyThankyou()).withNewSession
  }
}

case class SurveyFeedback(satisfaction: Satisfaction, details: String, journey: Journey)
