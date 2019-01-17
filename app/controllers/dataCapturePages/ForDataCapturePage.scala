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

package controllers.dataCapturePages

import actions.{RefNumAction, RefNumRequest}
import connectors._
import controllers._
import form.persistence.{BuildForm, FormDocumentRepository, SaveForm, SaveFormInRepository}
import models.journeys._
import models.pages.{Summary, SummaryBuilder}
import org.jboss.netty.handler.codec.http.QueryStringDecoder
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Format
import play.api.mvc._
import play.twirl.api.Html
import playconfig.{FormPersistence, SessionId}
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc.Results.Redirect
import ForDataCapturePage._
import form._
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import uk.gov.hmrc.http.HeaderCarrier

trait ForDataCapturePage[T] extends FrontendController {
  implicit val format: Format[T]
  implicit val ec: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def emptyForm: Form[T]

  def pageNumber: Int

  def saveForm: SaveForm = new SaveFormInRepository(FormPersistence.formDocumentRepository, SummaryBuilder)

  def repository: FormDocumentRepository = FormPersistence.formDocumentRepository

  def template(form: Form[T], summary: Summary)(implicit request: Request[AnyContent]): Html

  def show: Action[AnyContent] = RefNumAction.async { implicit request =>
    repository.findById(SessionId(hc), request.refNum) flatMap {
      case Some(doc) => showThisPageOrGoToNextAllowed(doc, request)
      case None =>
        Logger.error(s"Could not find document in current session - ${request.refNum} - ${hc.sessionId}")
        internalServerError(request)
    }
  }

  private def showThisPageOrGoToNextAllowed(doc: Document, request: RefNumRequest[AnyContent]): Future[Result] = {
    val sub = SummaryBuilder.build(doc)
    Journey.nextPageAllowable(pageNumber, sub) match {
      case PageToGoTo(page) if isThisPage(page) => displayForm(BuildForm(doc, page, emptyForm), sub, request)
      case p => RedirectTo(p, request.headers)
    }
  }

  private def isThisPage(page: Int) = page == pageNumber

  def save: Action[AnyContent] = RefNumAction.async { implicit request =>
    saveForm(request.body.asFormUrlEncoded, SessionId(hc), request.refNum, pageNumber) flatMap {
      case Some((savedFields, summary)) => goToNextPage(extractAction(request.body.asFormUrlEncoded), summary, savedFields)
      case None => internalServerError(request)
    }
  }

  def goToNextPage(action: FormAction, summary: Summary, savedFields: Map[String, Seq[String]])
    (implicit request: RefNumRequest[AnyContent]) = {
    action match {
      case Continue => bindForm(savedFields).fold(
        formWithErrors => displayForm(formWithErrors, summary, request),
        pageData => getPage(pageNumber + 1, summary, request)
      )
      case Update => bindForm(savedFields).fold(
        formWithErrors => displayForm(formWithErrors, summary, request),
        pageData => RedirectTo(Journey.pageToResumeAt(summary), request.headers)
      )
      case Save => Redirect(controllers.routes.SaveForLater.saveForLater())
      case Back => getPage(pageNumber - 1, summary, request)
      case Unknown => redirectToPage(pageNumber)
    }
  }

  private def bindForm(requestData: Map[String, Seq[String]]) = emptyForm.bindFromRequest(requestData).convertGlobalToFieldErrors()

  private def displayForm(form: Form[T], summary: Summary, request: RefNumRequest[AnyContent]) =
    request.flash.get(SaveForLater.s4lIndicator) match {
      case Some(_) => Ok(template(form.copy(errors = Seq.empty), summary)(request))
      case _ if form.hasErrors => BadRequest(template(form, summary)(request))
      case _ => Ok(template(form, summary)(request))
    }

  private def getPage(nextPage: Int, summary: Summary, request: RefNumRequest[AnyContent])(implicit hc: HeaderCarrier) = {
    val p = Journey.nextPageAllowable(nextPage, summary, Some(pageNumber))
    RedirectTo(p, request.headers)
  }

  private def redirectToPage(page: Int) = Redirect(routes.PageController.showPage(page))

  private def internalServerError(implicit rh: RequestHeader) = InternalServerError(views.html.error.error500())
}

object UrlFor {

  def apply(p: TargetPage, hs: Headers): String = UrlFor(actionFor(p), hs)

  def apply(c: Call, hs: Headers): String = {
    val referer = hs.get("referer")
    referer.flatMap(new QueryStringDecoder(_).getParameters.asScala.get("edit").map(_.asScala.head)).map { r =>
      c.url + "#" + r
    } getOrElse c.url
  }

  private def actionFor(p: TargetPage) = p match {
    case PageToGoTo(p) => dataCapturePages.routes.PageController.showPage(p)
    case SummaryPage => controllers.routes.Application.summary
  }
}

object RedirectTo {
  def apply(p: TargetPage, hs: Headers): Result = Redirect(UrlFor(p, hs))
}

object ForDataCapturePage {

  sealed trait FormAction
  object Continue extends FormAction
  object Back extends FormAction
  object Save extends FormAction
  object Update extends FormAction
  object Unknown extends FormAction

  def extractAction(fields: Option[Map[String, Seq[String]]]): FormAction = fields map { fs =>
    fs.get("continue_button").map(_ => Continue)
      .orElse(fs.get("save_button").map(_ => Save))
      .orElse(fs.get("back_button").map(_ => Back))
      .orElse(fs.get("update_button").map(_ => Update))
      .getOrElse(Unknown)
  } getOrElse(Unknown)
}
