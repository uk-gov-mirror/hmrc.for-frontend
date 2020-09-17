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
import views.html.{feedbackForm, inPageFeedbackThankyou}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FeedbackController @Inject()(cc: MessagesControllerComponents,
                                   http: ForHttp,
                                   sessionCookieCrypto: SessionCookieCrypto,
                                   repository: FormDocumentRepository,
                                   refNumAction: RefNumAction,
                                   override val servicesConfig: ServicesConfig,
                                   feedbackThankyouView :inPageFeedbackThankyou,
                                   feedbackFormView: feedbackForm
                        )(implicit ec: ExecutionContext) extends FrontendController(cc) with HMRCContact with HeaderCarrierForPartialsConverter  {

  override lazy val crypto = (value: String) => sessionCookieCrypto.crypto.encrypt(PlainText(value)).value
  val log = Logger(this.getClass)

  object FeedbackFormMapper{
    val feedbackForm = Form(
      mapping(
        "feedback-rating" -> nonEmptyText,
        "feedback-name" -> text,
        "feedback-email" -> text,
        "feedback-service" -> text,
        "feedback-referrer" -> text,
        "feedback-comments" -> optional(text)
      )(Feedback.apply)(Feedback.unapply)
    )
  }

  import FeedbackFormMapper.feedbackForm

  @deprecated("replace with 'feedback' function when GDS migration complete", "GDS Migration")
  def inPageFeedback = refNumAction.async { implicit request =>
    repository.findById(SessionId(headerCarrierForPartialsToHeaderCarrier), request.refNum) map {
      case Some(doc) => {
        val summary = SummaryBuilder.build(doc)
        Ok(views.html.inpagefeedback(hmrcBetaFeedbackFormUrl, None, summary))
      }
    }
  }

  @deprecated("replace with 'handleFeedbackSubmit' function when GDS migration complete", "GDS Migration")
  def sendBetaFeedbackToHmrc = refNumAction.async { implicit request: RefNumRequest[AnyContent] =>
    repository.findById(SessionId(headerCarrierForPartialsToHeaderCarrier), request.refNum) flatMap {
      case Some(doc) => {
        val summary = SummaryBuilder.build(doc)
        implicit val headerCarrier = hc.withExtraHeaders("Csrf-Token"-> "nocheck")
        request.body.asFormUrlEncoded.map { formData =>
          http.POSTForm[HttpResponse](hmrcSubmitBetaFeedbackUrl, formData)(readPartialsForm, hc(request),cc.executionContext ) map { res => res.status match {
            case 200 => Redirect(routes.FeedbackController.inPageFeedbackThankyou)
            case 400 => BadRequest(views.html.inpagefeedback(None, Html(res.body), summary))
            case _ => InternalServerError(views.html.feedbackError())
          }
          }
        }.getOrElse(throw new Exception("Empty Feedback Form"))
      }
    }
  }

  def handleFeedbackSubmit() = Action.async { implicit request =>
    val formUrlEncoded = request.body.asFormUrlEncoded
    feedbackForm.bindFromRequest().fold(
      formWithErrors => Future.successful({
        BadRequest(feedbackFormView(formWithErrors))
      }),
      validForm => {
        implicit val headerCarrier = hc.withExtraHeaders("Csrf-Token"-> "nocheck")
        http.POSTForm[HttpResponse](hmrcSubmitBetaFeedbackNoLoginUrl, formUrlEncoded.get)(readPartialsForm, hc(request),cc.executionContext ) map { res => res.status match {
          case 200 | 201 | 202 | 204 => {
            log.info("got 200 response: " + res.status)
          }
          case 400 => {
            log.warn("400 error from feedback Response")
          }
          case _ => {
            log.warn("got XXX response: " + res.status)
          }
        }
        }
        Redirect(routes.FeedbackController.inPageFeedbackThankyou)

      }
    )
  }

  @deprecated("replace with 'handleFeedbackSubmit' function when GDS migration complete", "GDS Migration")
  def sendBetaFeedbackToHmrcNoLogin = Action.async { implicit request =>
    request.body.asFormUrlEncoded.map { formData =>
      implicit val headerCarrier = hc.withExtraHeaders("Csrf-Token"-> "nocheck")
      http.POSTForm[HttpResponse](hmrcSubmitBetaFeedbackNoLoginUrl, formData)(readPartialsForm, hc(request),cc.executionContext ) map { res => res.status match {
        case 200 | 201 | 202 | 204 => {
          log.info(s"got ${res.status} response from $hmrcSubmitBetaFeedbackNoLoginUrl")
        }
        case _ =>{
          log.error (s"got ${res.status} response from $hmrcSubmitBetaFeedbackNoLoginUrl")
        }
      }
      }
    }
    Redirect(routes.FeedbackController.inPageFeedbackThankyou)
  }

  @deprecated("replace with 'feedback' function when GDS migration complete")
  def inPageFeedbackNoLogin = Action { implicit request =>
    Ok(views.html.inpagefeedbackNoLogin(hmrcBetaFeedbackFormNoLoginUrl))
  }

  def feedback = Action { implicit request =>
    Ok(feedbackFormView(feedbackForm))
  }

  def inPageFeedbackThankyou = Action { implicit request =>
    Ok(feedbackThankyouView())
  }
}

trait HMRCContact {

  def servicesConfig: ServicesConfig

  val contactFrontendPartialBaseUrl = servicesConfig.baseUrl("contact-frontend")
  val serviceIdentifier = "RALD"

  val betaFeedbackSubmitUrl = routes.FeedbackController.sendBetaFeedbackToHmrc().url
  val feedbackUrl = routes.FeedbackController.feedback().url
  val betaFeedbackSubmitUrlNoLogin = routes.FeedbackController.sendBetaFeedbackToHmrcNoLogin().url
  val hmrcSubmitBetaFeedbackUrl = s"$contactFrontendPartialBaseUrl/contact/beta-feedback/form?resubmitUrl=${urlEncode(betaFeedbackSubmitUrl)}"
  val hmrcSubmitFeedbackUrl = s"$contactFrontendPartialBaseUrl/contact/beta-feedback/form?resubmitUrl=${urlEncode(feedbackUrl)}"
  val hmrcSubmitBetaFeedbackNoLoginUrl = s"$contactFrontendPartialBaseUrl/contact/beta-feedback/form?resubmitUrl=${urlEncode(betaFeedbackSubmitUrlNoLogin)}"
  val hmrcBetaFeedbackFormUrl = s"$contactFrontendPartialBaseUrl/contact/beta-feedback/form?service=$serviceIdentifier&submitUrl=${urlEncode(betaFeedbackSubmitUrl)}"
  val hmrcBetaFeedbackFormNoLoginUrl = s"$contactFrontendPartialBaseUrl/contact/beta-feedback/form?service=$serviceIdentifier&submitUrl=${urlEncode(betaFeedbackSubmitUrlNoLogin)}"

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