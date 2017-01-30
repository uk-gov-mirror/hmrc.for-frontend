/*
 * Copyright 2017 HM Revenue & Customs
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

import actions.RefNumAction
import controllers._
import form.persistence.FormDocumentRepository
import models.pages.SummaryBuilder
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Results._
import play.api.mvc._
import play.twirl.api.Html
import playconfig.{FormPersistence, SessionId}
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.frontend.filters.SessionCookieCryptoFilter
import uk.gov.hmrc.play.http._
import uk.gov.hmrc.play.language.LanguageUtils
import uk.gov.hmrc.play.partials._

object Feedback extends HeaderCarrierForPartialsConverter {
  import controllers.feedback.HMRCContact._ // scalastyle:ignore

  def repository: FormDocumentRepository = FormPersistence.formDocumentRepository

  override val crypto = SessionCookieCryptoFilter.encrypt _
  val http = playconfig.WSHttp

  def inPageFeedback = RefNumAction.async { implicit request =>
    repository.findById(SessionId(headerCarrierForPartialsToHeaderCarrier), request.refNum) map {
      case Some(doc) => {
        val summary = SummaryBuilder.build(doc)
        Ok(views.html.inpagefeedback(hmrcBetaFeedbackFormUrl, None, summary)(request, LanguageUtils.getCurrentLang))
      }
    }
  }

  def sendBetaFeedbackToHmrc = RefNumAction.async { implicit request =>
    repository.findById(SessionId(headerCarrierForPartialsToHeaderCarrier), request.refNum) flatMap {
      case Some(doc) => {
        val summary = SummaryBuilder.build(doc)
        request.body.asFormUrlEncoded.map { formData =>
          http.POSTForm[HttpResponse](hmrcSubmitBetaFeedbackUrl, formData) map { res => res.status match {
            case 200 => Redirect(routes.Feedback.inPageFeedbackThankyou)
            case 400 => BadRequest(views.html.inpagefeedback(None, Html(res.body), summary))
            case _ => InternalServerError(views.html.feedbackError()(request, LanguageUtils.getCurrentLang))
          }
          }
        }.getOrElse(throw new Exception("Empty Feedback Form"))
      }
    }
  }

  def sendBetaFeedbackToHmrcNoLogin = Action.async { implicit request =>
    request.body.asFormUrlEncoded.map { formData =>
      http.POSTForm[HttpResponse](hmrcSubmitBetaFeedbackNoLoginUrl, formData) map { res => res.status match {
        case 200 => Redirect(routes.Feedback.inPageFeedbackThankyou)
        case 400 => BadRequest(views.html.inpagefeedbackNoLogin(None, Html(res.body)))
        case _ => InternalServerError(views.html.feedbackError()(request, LanguageUtils.getCurrentLang))
      }
      }
    }.getOrElse(throw new Exception("Empty Feedback Form"))
  }

  def inPageFeedbackNoLogin = Action { implicit request =>
    Ok(views.html.inpagefeedbackNoLogin(hmrcBetaFeedbackFormNoLoginUrl))
  }

  def inPageFeedbackThankyou = Action { implicit request =>
    Ok(views.html.inPageFeedbackThankyou())
  }
}

//scalastyle:off line.size.limit
object HMRCContact extends ServicesConfig {
  val contactFrontendPartialBaseUrl = baseUrl("contact-frontend")
  val serviceIdentifier = "RALD"

  val betaFeedbackSubmitUrl = routes.Feedback.sendBetaFeedbackToHmrc().url
  val betaFeedbackSubmitUrlNoLogin = routes.Feedback.sendBetaFeedbackToHmrcNoLogin().url
  val hmrcSubmitBetaFeedbackUrl = s"$contactFrontendPartialBaseUrl/contact/beta-feedback/form?resubmitUrl=${urlEncode(betaFeedbackSubmitUrl)}"
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
