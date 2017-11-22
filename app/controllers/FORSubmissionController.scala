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

package controllers

import actions.RefNumAction
import connectors.SubmissionConnector
import form.persistence.FormDocumentRepository
import helpers.AddressAuditing
import metrics.Metrics
import models.pages.SummaryBuilder
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import playconfig.{Audit, FormPersistence, SessionId}
import useCases.{SubmissionBuilder, SubmitBusinessRentalInformation}

import scala.concurrent.Future
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import uk.gov.hmrc.http.{HeaderCarrier, Upstream4xxResponse}
import uk.gov.hmrc.play.HeaderCarrierConverter

object FORSubmissionController extends FORSubmissionController {
  protected lazy val documentRepo: FormDocumentRepository = FormPersistence.formDocumentRepository
  protected lazy val auditAddresses = AddressAuditing
  private lazy val submitBri = SubmitBusinessRentalInformation(documentRepo, SubmissionBuilder, SubmissionConnector)

  def submitBusinessRentalInformation: SubmitBusinessRentalInformation = submitBri
}

trait FORSubmissionController extends Controller {
  protected val documentRepo: FormDocumentRepository
  protected val auditAddresses: AddressAuditing
  lazy val confirmationUrl = controllers.feedback.routes.Survey.confirmation().url

  def submit: Action[AnyContent] = RefNumAction.async { implicit request =>
    request.body.asFormUrlEncoded.flatMap { body =>
      body.get("declaration").map { agree =>
        if (agree.headOption.exists(_.toBoolean)) submit(request.refNum) else rejectSubmission
      }
    } getOrElse rejectSubmission
  }

  private def submit[T](refNum: String)(implicit request: Request[T]): Future[Result] = {
    val hc = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))
    for {
      sub <- submitBusinessRentalInformation(refNum)(hc)
      _ <- Audit("FormSubmission", Map("referenceNumber" -> refNum, "submitted" -> DateTime.now.toString,
        "name" -> sub.customerDetails.map(_.fullName).getOrElse("")))
      _ <- auditAddress(refNum, request)
    } yield {
      Metrics.submissions.mark()
      Found(confirmationUrl)
    }
  }recoverWith { case Upstream4xxResponse(_, 409, _, _) => Conflict(views.html.error.error409()) }

  protected def auditAddress[T](refNum: String, request: Request[_]): Future[Unit] = {
    val hc = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))
    documentRepo.findById(SessionId(hc), refNum) flatMap {
      case Some(doc) =>
        val s = SummaryBuilder.build(doc)
        auditAddresses(s, request)
      case None => ()
    }
  }

  private def rejectSubmission = Future.successful {
    Found(routes.Application.declarationError().url)
  }

  def submitBusinessRentalInformation: SubmitBusinessRentalInformation
}
