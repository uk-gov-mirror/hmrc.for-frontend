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
class Application @Inject() (cc:MessagesControllerComponents,
                 refNumAction: RefNumAction,
                 pdfGenerator: PdfGenerator,
                  repository: FormDocumentRepository,
                  summaryView:views.html.summary
                 )(implicit ec: ExecutionContext) extends FrontendController(cc) {


  def declaration = refNumAction.async { implicit request =>
    repository.findById(SessionId(hc), request.refNum).map {
      case Some(doc) =>
        val summary = SummaryBuilder.build(doc)
        val fn = summary.customerDetails.map(_.fullName).getOrElse("")
        val ut = summary.customerDetails.map(_.userType.name).getOrElse("")
        Ok(views.html.declaration(Form(("", text)), fn, ut, summary: Summary))
      case None => InternalServerError(views.html.error.error500())
    }
  }

  def declarationError = refNumAction.async { implicit request =>
    repository.findById(SessionId(hc), request.refNum).map {
      case Some(doc) =>
        val summary = SummaryBuilder.build(doc)
        val fn = summary.customerDetails.map(_.fullName).getOrElse("")
        val ut = summary.customerDetails.map(_.userType.name).getOrElse("")
        Ok(views.html.declaration(Form(("", text)).withError("declaration", Errors.declaration), fn, ut, summary))
      case None => InternalServerError(views.html.error.error500())
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

  def sessionTimeout = Action { implicit request => Ok(views.html.sessionTimeout()) }

  def cookies = Action { implicit request =>
    Ok(views.html.info.cookies())
  }

  def termsandconditions = Action { implicit request =>
    Ok(views.html.info.termsAndConditions())
  }

  def error404 = Action { implicit request =>
    Ok(views.html.error.error404())
  }

  def error408 = Action { implicit request =>
    Ok(views.html.error.error408())
  }

  def error409 = Action { implicit request =>
    Ok(views.html.error.error409())
  }

  def error410 = Action { implicit request =>
    Ok(views.html.error.error410())
  }

  def error500 = Action { implicit request =>
    Ok(views.html.error.error500())
  }

  def inpageVacatedForm = refNumAction.async { implicit request =>
    repository.findById(SessionId(hc), request.refNum).map {
      case Some(doc) => Ok(views.html.inpageVacatedForm(Some(SummaryBuilder.build(doc))))
      case _ => InternalServerError(views.html.error.error500())
    }
  }

  private def host(implicit request: RequestHeader): String = {
    s"${ForConfig.pdfProtocol}://${request.host}/"
  }

  def pdf = refNumAction.async { implicit request =>
    repository.findById(SessionId(hc), request.refNum).map {
      case Some(doc) =>
        val summary = SummaryBuilder.build(doc)
        val pdf = pdfGenerator.toBytes(summaryView(summary), host)
        Ok(pdf).as("application/pdf")
      case None =>
        InternalServerError(views.html.error.error500())
    }
  }

  def summary = refNumAction.async { implicit request =>
    repository.findById(SessionId(hc), request.refNum).map {
      case Some(doc) =>
        val sub = SummaryBuilder.build(doc)
        Ok(summaryView(sub))
      case None =>
        InternalServerError(views.html.error.error500())
    }
  }

  def docs = Action { implicit request =>
    Ok(views.html.api.apidoc())
  }
}
