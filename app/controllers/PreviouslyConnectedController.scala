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

package controllers

import actions.{RefNumAction, RefNumRequest}
import form.PreviouslyConnectedForm.formMapping
import javax.inject.{Inject, Singleton}
import models.pages.SummaryBuilder
import play.api.{Configuration, Logger}
import play.api.i18n.{I18nSupport, MessagesApi}
import playconfig.{FormPersistence, S4L, SessionId}
import uk.gov.hmrc.http.cache.client.ShortLivedCache
import uk.gov.hmrc.play.frontend.controller.FrontendController
import _root_.form.persistence.FormDocumentRepository
import models.serviceContracts.submissions.PreviouslyConnected.format
import uk.gov.hmrc.http.logging.LoggingDetails
import PreviouslyConnectedController.cacheKey

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PreviouslyConnectedController @Inject()(ec: ExecutionContext, val cache: ShortLivedCache = S4L,
                                              val repository: FormDocumentRepository = FormPersistence.formDocumentRepository)
                                             (implicit val messagesApi: MessagesApi) extends FrontendController with I18nSupport {
  val logger = Logger(this.getClass)

  /**
    * Not required anymore, use EC from IOC(guice)
    * @param loggingDetails
    * @return
    */
  override implicit def mdcExecutionContext(implicit loggingDetails: LoggingDetails): ExecutionContext = ec

  def findSummary(implicit request: RefNumRequest[_]) = {
    repository.findById(SessionId(hc), request.refNum) flatMap {
      case Some(doc) => Option(SummaryBuilder.build(doc))
      case None => None
    }
  }


  def onPageView = RefNumAction.async { implicit request =>
    findSummary.map {
      case Some(summary) => Ok(views.html.previouslyConnected(formMapping, summary))
      case None => {
        logger.warn(s"Could not find document in current session - ${request.refNum} - ${hc.sessionId}")
        InternalServerError(views.html.error.error500())
      }
    }
  }

  def onPageSubmit = RefNumAction.async { implicit request =>
    findSummary.flatMap {
      case Some(summary) => {
        formMapping.bindFromRequest().fold( formWithErrors => {
          Future.successful(Ok(views.html.previouslyConnected(formWithErrors, summary)))
        }, {formWithData =>
          cache.cache(SessionId(hc), cacheKey, formWithData).map { cacheWriteResult =>
            Redirect(routes.NotConnectedController.onPageView())
          }
        })
      }
      case None => {
        logger.warn(s"Could not find document in current session - ${request.refNum} - ${hc.sessionId}")
        Future.successful(InternalServerError(views.html.error.error500()))
      }
    }
  }
}

object PreviouslyConnectedController {
  val cacheKey = "PreviouslyConnected"
}