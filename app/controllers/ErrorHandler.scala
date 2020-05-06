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

import javax.inject.{Inject, Singleton}
import play.api.i18n.MessagesApi
import play.api.mvc.{Request, RequestHeader, Result}
import play.twirl.api.Html
import uk.gov.hmrc.http.{BadRequestException, NotFoundException, Upstream4xxResponse}
import uk.gov.hmrc.play.bootstrap.http.FrontendErrorHandler

import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject() (val messagesApi: MessagesApi) extends FrontendErrorHandler {

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    import play.api.mvc.Results._
    implicit val requestHeader: RequestHeader = request
    exception.getCause match {
      case e: BadRequestException => BadRequest(views.html.error.error500())
      case Upstream4xxResponse(_, 404, _, _) => NotFound(views.html.error.error404())
      case Upstream4xxResponse(_, 408, _, _) => RequestTimeout(views.html.error.error408())
      case Upstream4xxResponse(_, 409, _, _) => Conflict(views.html.error.error409())
      case Upstream4xxResponse(_, 410, _, _) => Gone(views.html.error.error410())
      case e: NotFoundException => NotFound(views.html.error.error404())
      case _ => super.resolveError(request, exception)
    }
  }

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit request: Request[_]): Html = {
    views.html.error.error500()
  }

  override def notFoundTemplate(implicit request: Request[_]): Html = {
    views.html.error.error404()
  }
}
