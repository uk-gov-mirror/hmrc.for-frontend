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

package controllers.feedback

import java.net.URLEncoder

import actions.{RefNumAction, RefNumRequest}
import connectors.ForHttp
import controllers._
import form.Errors
import form.persistence.FormDocumentRepository
import javax.inject.{Inject, Singleton}
import models.Feedback
import models.pages.SummaryBuilder
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, optional, text}
import play.api.mvc._
import play.api.{Logger, Play}
import play.twirl.api.Html
import playconfig.SessionId
import uk.gov.hmrc.crypto.PlainText
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.play.bootstrap.filters.frontend.crypto.SessionCookieCrypto
import uk.gov.hmrc.play.partials._
import views.html.{feedbackForm, feedbackThx}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FeedbackController @Inject()(cc: MessagesControllerComponents,
                                   http: ForHttp,
                                   sessionCookieCrypto: SessionCookieCrypto,
                                   repository: FormDocumentRepository,
                                   refNumAction: RefNumAction,
                                   override val servicesConfig: ServicesConfig,
                                   feedbackThankyouView :feedbackThx,
                                   feedbackFormView: feedbackForm
                        )(implicit ec: ExecutionContext) extends FrontendController(cc) with HMRCContact with HeaderCarrierForPartialsConverter  {

  override lazy val crypto = (value: String) => sessionCookieCrypto.crypto.encrypt(PlainText(value)).value
  val log = Logger(this.getClass)

  object FeedbackFormMapper{
    val feedbackForm = Form(
      mapping(
        "feedback-rating" -> optional(text).verifying("feedback.rating.required", _.isDefined),
        "feedback-name" -> text,
        "feedback-email" -> text,
        "service" -> text,
        "referrer" -> text,
        "feedback-comments" -> optional(text)
      )(Feedback.apply)(Feedback.unapply)
    )
  }

  import FeedbackFormMapper.feedbackForm

  def handleFeedbackSubmit() = Action.async { implicit request =>
    val formUrlEncoded = request.body.asFormUrlEncoded
    feedbackForm.bindFromRequest().fold(
      formWithErrors => Future.successful({
        BadRequest(feedbackFormView(formWithErrors))
      }),
      validForm => {
        implicit val headerCarrier = hc.withExtraHeaders("Csrf-Token"-> "nocheck")
        http.POSTForm[HttpResponse](contactFrontendFeedbackPostUrl, formUrlEncoded.get)(readPartialsForm, headerCarrier, ec ) map { res => res.status match {
          case 200 | 201 | 202 | 204 => log.info(s"Feedback successful: ${res.status} response from $contactFrontendFeedbackPostUrl")
          case _ => log.error (s"Feedback FAILED: ${res.status} response from $contactFrontendFeedbackPostUrl, \nparams: ${formUrlEncoded.get}, \nheaderCarrier: ${headerCarrier}")
        }
        }
        Redirect(routes.FeedbackController.feedbackThankyou)
      }
    )
  }

  def feedback = Action { implicit request =>
    Ok(feedbackFormView(feedbackForm))
  }

  def feedbackThankyou = Action { implicit request =>
    Ok(feedbackThankyouView())
  }
}

trait HMRCContact {

  def servicesConfig: ServicesConfig

  val contactFrontendPartialBaseUrl = servicesConfig.baseUrl("contact-frontend")
  val serviceIdentifier = "RALD"
  val feedbackUrl = routes.FeedbackController.feedback().url
  val contactFrontendFeedbackPostUrl = s"$contactFrontendPartialBaseUrl/contact/beta-feedback/submit-unauthenticated"
  val hmrcSubmitFeedbackUrl = s"$contactFrontendPartialBaseUrl/contact/beta-feedback/form?resubmitUrl=${urlEncode(feedbackUrl)}"
  val hmrcHelpWithPageFormUrl = s"$contactFrontendPartialBaseUrl/contact/problem_reports_ajax?service=$serviceIdentifier"


  // The default HTTPReads will wrap the response in an exception and make the body inaccessible
  implicit val readPartialsForm: HttpReads[HttpResponse] = new HttpReads[HttpResponse] {
    def read(method: String, url: String, response: HttpResponse) = response
  }

  private def urlEncode(value: String) = URLEncoder.encode(value, "UTF-8")
}

@deprecated
object HMRCContact {
  def apply(): HMRCContact = {
    val config = Play.current.injector.instanceOf[ServicesConfig]
    new HMRCContactImpl(config)
  }

  private class HMRCContactImpl(val servicesConfig: ServicesConfig) extends HMRCContact

}