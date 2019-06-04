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

import actions.RefNumRequest
import config.ForConfig
import controllers.dataCapturePages.ForDataCapturePage.FormAction
import form.PageZeroForm.pageZeroForm
import models._
import models.pages.Summary
import play.api.Play
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html
import play.api.i18n.Messages.Implicits._
import play.api.Play.current

trait PageZeroController extends ForDataCapturePage[Boolean] {
  override implicit val format: Format[Boolean] = Format(Reads.BooleanReads, Writes.BooleanWrites)

  val emptyForm = pageZeroForm
  val pageNumber: Int = 0

  def template(form: Form[Boolean], summary: Summary)(implicit request: Request[AnyContent]): Html = {
    views.html.part0(form, summary)
  }

  override def goToNextPage(action: FormAction, summary: Summary, savedFields: Map[String, Seq[String]])(implicit request: RefNumRequest[AnyContent]) =
    savedFields.get("isRelated") match {
      case Some(Seq("false")) if ForConfig.showNewNotConnectedPage => Redirect(controllers.routes.NotConnectedController.onPageView())
      case Some(Seq("false")) => Redirect(controllers.routes.Application.inpageVacatedForm())
      case _ => super.goToNextPage(action, summary, savedFields)
    }
}

object PageZeroController extends PageZeroController