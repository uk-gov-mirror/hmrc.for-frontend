/*
 * Copyright 2022 HM Revenue & Customs
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
import form.persistence.FormDocumentRepository
import helpers.AddressAuditing
import models.Addresses

import javax.inject.{Inject, Singleton}
import models.pages.SummaryBuilder
import models.serviceContracts.submissions.Submission
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import playconfig.SessionId
import uk.gov.hmrc.http.{HeaderCarrier, Upstream4xxResponse}
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import useCases.SubmitBusinessRentalInformation

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FORSubmissionController @Inject() (cc: MessagesControllerComponents,
                                         documentRepo: FormDocumentRepository,
                                         audit: connectors.Audit,
                                         refNumberAction: RefNumAction,
                                         submitBusinessRentalInformation: SubmitBusinessRentalInformation,
                                         auditAddresses: AddressAuditing,
                                        errorView: views.html.error.error
                                        )(implicit ec: ExecutionContext) extends FrontendController(cc) {

  lazy val confirmationUrl = controllers.feedback.routes.SurveyController.confirmation.url

  def submit: Action[AnyContent] = refNumberAction.async { implicit request:RefNumRequest[AnyContent] =>
    request.body.asFormUrlEncoded.flatMap { body =>
      body.get("declaration").map { agree =>
        if (agree.headOption.exists(_.toBoolean)) submit(request.refNum) else rejectSubmission
      }
    } getOrElse rejectSubmission
  }

  private def submit[T](refNum: String)(implicit request: RefNumRequest[T]): Future[Result] = {
    val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    for {
      submission <- submitBusinessRentalInformation(refNum)(hc)
      _ <- auditFormSubmissionAndAddress(submission, refNum)(hc, request)
    } yield {
      // Metrics.submissions.mark() //TODO - Solve metrics
      Found(confirmationUrl)
    }
  }recoverWith { case Upstream4xxResponse(_, 409, _, _) => Conflict(errorView(409)) }

  private def auditFormSubmissionAndAddress[T](submission: Submission, refNum: String)
                                              (implicit hc: HeaderCarrier, request: RefNumRequest[T]): Future[Unit] = {
    val submissionJson = Json.toJson(submission).as[JsObject]

    documentRepo.findById(SessionId(hc), refNum).flatMap {
      case Some(doc) =>
        val summary = SummaryBuilder.build(doc)
        auditAddresses(summary, request)
        submissionJson ++ Addresses.addressJson(summary)
      case None => submissionJson
    }.map(jsObject => audit.sendExplicitAudit("FormSubmission", jsObject))
  }

  private def rejectSubmission = Future.successful {
    Found(routes.ApplicationController.declarationError.url)
  }

}
