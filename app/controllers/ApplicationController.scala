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

import actions.RefNumAction
import config.ForConfig
import form.Errors
import form.persistence.FormDocumentRepository
import it.innove.play.pdf.PdfGenerator
import javax.inject.{Inject, Singleton}
import models.pages._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import playconfig.SessionId
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.ExecutionContext

@Singleton
class ApplicationController @Inject()(cc: MessagesControllerComponents,
                                      refNumAction: RefNumAction,
                                      pdfGenerator: PdfGenerator,
                                      repository: FormDocumentRepository,
                                      checkYourAnswersView: views.html.checkYourAnswers,
                                      declarationView: views.html.declaration,
                                      printAnswersView: views.html.print,
                                     errorView: views.html.error.error,
                                     sessionTimeoutView: views.html.sessionTimeout
                                     )(implicit ec: ExecutionContext) extends FrontendController(cc) {



  def declaration = refNumAction.async { implicit request =>
    repository.findById(SessionId(hc), request.refNum).map {
      case Some(doc) =>
        val summary = SummaryBuilder.build(doc)
        val fullName = summary.customerDetails.map(_.fullName).getOrElse("")
        val userType = summary.customerDetails.map(_.userType.name).getOrElse("")
        Ok(declarationView(Form(("", text)), fullName, userType, summary: Summary))
      case None => InternalServerError(errorView(500))
    }
  }

  def declarationError = refNumAction.async { implicit request =>
    repository.findById(SessionId(hc), request.refNum).map {
      case Some(doc) =>
        val summary = SummaryBuilder.build(doc)
        val fullName = summary.customerDetails.map(_.fullName).getOrElse("")
        val userType = summary.customerDetails.map(_.userType.name).getOrElse("")
        Ok(declarationView(Form(("", text)).withError("declaration", Errors.declaration), fullName, userType, summary))
      case None => InternalServerError(errorView(500))
    }
  }

  def fail = Action { implicit request =>
    Ok(views.html.fail())
  }

  def startAgain = refNumAction.async { implicit request =>
    repository.clear(SessionId(hc), request.refNum) map { _ =>
      Redirect(dataCapturePages.routes.PageController.showPage(0))
    }
  }

  def index = Action { implicit request =>
    if(ForConfig.startPageRedirect){
      Redirect(ForConfig.govukStartPage)

    }else{
      Ok(views.html.index())
    }
  }

  def sessionTimeout = Action { implicit request => Ok(sessionTimeoutView()) }

  def error404 = Action { implicit request =>
    Ok(errorView(404))
  }

  def error408 = Action { implicit request =>
    Ok(errorView(408))
  }

  def error409 = Action { implicit request =>
    Ok(errorView(409))
  }

  def error410 = Action { implicit request =>
    Ok(errorView(410))
  }

  def error500 = Action { implicit request =>
    Ok(errorView(500))
  }

  def inpageVacatedForm = refNumAction.async { implicit request =>
    repository.findById(SessionId(hc), request.refNum).map {
      case Some(doc) => Ok(views.html.inpageVacatedForm(Some(SummaryBuilder.build(doc))))
      case _ => InternalServerError(errorView(500))
    }
  }

  private def host(implicit request: RequestHeader): String = {
    s"${ForConfig.pdfProtocol}://${request.host}/"
  }

  def checkYourAnswers = refNumAction.async { implicit request =>
    repository.findById(SessionId(hc), request.refNum).map {
      case Some(doc) =>
        val sub = SummaryBuilder.build(doc)
        Ok(checkYourAnswersView(sub))
      case None =>
        InternalServerError(errorView(500))
    }
  }

  def print = refNumAction.async { implicit request =>
    repository.findById(SessionId(hc), request.refNum).map {
      case Some(doc) =>
        val sub = SummaryBuilder.build(doc)
        Ok(printAnswersView(sub))
      case None =>
        InternalServerError(errorView(500))
    }
  }

  def docs = Action { implicit request =>
    Ok(views.html.api.apidoc())
  }
}
