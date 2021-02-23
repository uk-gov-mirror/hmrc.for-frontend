/*
 * Copyright 2021 HM Revenue & Customs
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

import actions.{RefNumAction, RefNumRequest}
import connectors.{Audit, SubmissionConnector}
import controllers.feedback.Survey
import form.NotConnectedPropertyForm
import form.persistence.{FormDocumentRepository, MongoSessionRepository}
import form.NotConnectedPropertyForm.form
import models.NotConnectedJourney
import models.pages.{Summary, SummaryBuilder}
import models.serviceContracts.submissions.{NotConnectedSubmission, PreviouslyConnected}
import play.api.libs.json.Json
import play.api.mvc.MessagesControllerComponents
import play.api.{Configuration, Logger}
import playconfig.SessionId
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class NotConnectedSummaryController @Inject()
( configuration: Configuration,
  repository: FormDocumentRepository,
  submissionConnector: SubmissionConnector,
  refNumAction: RefNumAction,
  cache: MongoSessionRepository,
  audit: Audit,
  cc: MessagesControllerComponents,
  notConnectedView:views.html.notConnectedSummary,
  confirmNotConnectedView: views.html.confirmNotConnected,
  errorView: views.html.error.error
  )(implicit ec: ExecutionContext)
                                       extends FrontendController(cc) {

  val logger = Logger(classOf[NotConnectedSummaryController])

  def findSummary(implicit request: RefNumRequest[_]) = {
    repository.findById(SessionId(hc), request.refNum) flatMap {
      case Some(doc) => Option(SummaryBuilder.build(doc))
      case None => None
    }
  }

  def removeSession(implicit request: RefNumRequest[_]) = {
    repository.remove(SessionId(hc))
  }

  def onPageView = refNumAction.async { implicit request =>
    findSummary.map {
      case Some(summary) => Ok(notConnectedView(form, summary))
      case None => {
        logger.error(s"Could not find document in current session - ${request.refNum} - ${hc.sessionId}")
        InternalServerError(errorView(500))
      }
    }
  }

  def onPageSubmit = refNumAction.async { implicit request =>

    findSummary.flatMap {
      case Some(summary) => {
        form.bindFromRequest().fold({ formWithErrors =>
          Future.successful(Ok(notConnectedView(formWithErrors, summary)))
        }, { formWithData => {
          audit.sendExplicitAudit("NotConnectedSubmission",
            Json.obj(Audit.referenceNumber -> summary.referenceNumber))
          submitToHod(formWithData, summary).map { _ =>
            Redirect(routes.NotConnectedController.onConfirmationView)
          }.recover {
            case e: Exception => {
              logger.error(s"Could not send data to HOD - ${request.refNum} - ${hc.sessionId}")
              InternalServerError(errorView(500))
            }
          }
        }})
      }
      case None => {
        logger.error(s"Could not find document in current session - ${request.refNum} - ${hc.sessionId}")
        InternalServerError(errorView(500))
      }
    }
  }

  def onConfirmationView() = refNumAction.async { implicit request =>
    val feedbackForm = Survey.completedFeedbackForm.bind(
      Map("journey" -> NotConnectedJourney.name)
    ).discardingErrors

    findSummary.flatMap {
      case Some(summary) => {
          removeSession
          .map(_ => Ok(confirmNotConnectedView(summary, feedbackForm)))
      } //.withNewSession
      case None => {
        logger.error(s"Could not find document in current session - ${request.refNum} - ${hc.sessionId}")
        Future.successful(InternalServerError(errorView(500)))
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
