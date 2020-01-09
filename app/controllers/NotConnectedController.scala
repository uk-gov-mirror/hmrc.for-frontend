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

import java.time.Instant

import actions.{RefNumAction, RefNumRequest}
import connectors.SubmissionConnector
import controllers.feedback.{Survey, SurveyFeedback}
import form.NotConnectedPropertyForm
import form.persistence.{FormDocumentRepository, MongoSessionRepository}
import form.NotConnectedPropertyForm.form
import javax.inject.{Inject, Singleton}
import models.{NotConnectedJourney, Satisfaction}
import models.pages.{Summary, SummaryBuilder}
import models.serviceContracts.submissions.{NotConnectedSubmission, PreviouslyConnected}
import play.api.data.Forms.{mapping, text}
import play.api.data.{Form, Forms}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.{Configuration, Logger}
import playconfig.{FormPartialProvider, FormPersistence, SessionId}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future


@Singleton
class NotConnectedController @Inject()(configuration: Configuration, submissionConnector: SubmissionConnector, cache: MongoSessionRepository)
                                      (implicit val messagesApi: MessagesApi) extends FrontendController with I18nSupport {

  val logger = Logger(classOf[NotConnectedController])
  def repository: FormDocumentRepository = FormPersistence.formDocumentRepository

  def findSummary(implicit request: RefNumRequest[_]) = {
    repository.findById(SessionId(hc), request.refNum) flatMap {
      case Some(doc) => Option(SummaryBuilder.build(doc))
      case None => None
    }
  }

  def clearSummary(implicit request: RefNumRequest[_]) = {
    repository.clear(SessionId(hc), request.refNum)
  }

  def onPageView = RefNumAction.async { implicit request =>
    findSummary.map {
      case Some(summary) => Ok(views.html.notConnected(form, summary))
      case None => {
        logger.error(s"Could not find document in current session - ${request.refNum} - ${hc.sessionId}")
        InternalServerError(views.html.error.error500())
      }
    }
  }

  def onPageSubmit = RefNumAction.async { implicit request =>

    findSummary.flatMap {
      case Some(summary) => {
        form.bindFromRequest().fold({ formWithErrors =>
          Future.successful(Ok(views.html.notConnected(formWithErrors, summary)))
        }, { formWithData =>
          submitToHod(formWithData, summary).map { _ =>
            Redirect(routes.NotConnectedController.onConfirmationView)
          }.recover {
            case e: Exception => {
              logger.error(s"Could not send data to HOD - ${request.refNum} - ${hc.sessionId}")
              InternalServerError(views.html.error.error500())
            }
          }
        })
      }
      case None => {
        logger.error(s"Could not find document in current session - ${request.refNum} - ${hc.sessionId}")
        InternalServerError(views.html.error.error500())
      }
    }
  }

  def onConfirmationView() = RefNumAction.async { implicit request =>
    val feedbackForm = Survey.completedFeedbackForm.bind(
      Map("journey" -> NotConnectedJourney.name)
    ).discardingErrors

    findSummary.map {
      case Some(summary) => Ok(views.html.confirmNotConnected(summary, feedbackForm)) //.withNewSession
      case None => {
        logger.error(s"Could not find document in current session - ${request.refNum} - ${hc.sessionId}")
        InternalServerError(views.html.error.error500())
      }
    }
  }

  def getPreviouslyConnectedFromCache()(implicit hc: HeaderCarrier)  = {
    cache.fetchAndGetEntry[PreviouslyConnected](SessionId(hc), PreviouslyConnectedController.cacheKey).flatMap {
      case Some(x) => Future.successful(x)
      case None => Future.failed(new RuntimeException("Unable to find record in cache for previously connected"))

    }

  }

  private def submitToHod(submissionForm: NotConnectedPropertyForm, summary: Summary)(implicit hc: HeaderCarrier) = {
    getPreviouslyConnectedFromCache().flatMap { previouslyConnected =>
      val submission = NotConnectedSubmission(
        summary.referenceNumber,
        summary.address.get,
        submissionForm.fullName,
        submissionForm.email,
        submissionForm.phoneNumber,
        submissionForm.additionalInformation,
        Instant.now(),
        previouslyConnected.previouslyConnected
      )
      submissionConnector.submitNotConnected(summary.referenceNumber, submission)
    }
  }

}
