/*
 * Copyright 2016 HM Revenue & Customs
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
import org.joda.time.DateTime
import play.api.mvc.{Action, AnyContent, Request}
import playconfig.{FormPersistence, Audit}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import useCases.{SubmissionBuilder, SubmitBusinessRentalInformation}

import scala.concurrent.Future

object FORSubmissionController extends FORSubmissionController {
	private lazy val submitBri = SubmitBusinessRentalInformation(FormPersistence.formDocumentRepository, SubmissionBuilder, SubmissionConnector)
	def submitBusinessRentalInformation: SubmitBusinessRentalInformation = submitBri
}

trait FORSubmissionController extends FrontendController { 

  def submit: Action[AnyContent] = RefNumAction.async { implicit request =>
		request.body.asFormUrlEncoded.flatMap { body =>
			body.get("declaration").map { agree =>				
				if(agree.headOption.map(_.toBoolean).getOrElse(false)) submit(request.refNum) else rejectSubmission
			}
		} getOrElse rejectSubmission
	}

	private def submit[T](refNum: String)(implicit request: Request[T]) = 
		submitBusinessRentalInformation(refNum).map { sub =>
			Audit("FormSubmission", Map("referenceNumber" -> refNum, "submitted" -> DateTime.now.toString, "name" -> sub.customerDetails.map(_.fullName).getOrElse("")))
			Found(feedback.routes.Survey.confirmation.url)
		}

	private def rejectSubmission = Future.successful { Found(routes.Application.declarationError.url) }

  def submitBusinessRentalInformation: SubmitBusinessRentalInformation
}
