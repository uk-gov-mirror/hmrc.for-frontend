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

package controllers.dataCapturePages

import actions.{RefNumAction, RefNumRequest}
import form.PageFourForm.pageFourForm
import javax.inject.Inject
import models._
import models.pages._
import models.serviceContracts.submissions.{UserTypeOccupiersAgent, UserTypeOwnersAgent}
import play.api.data.Form
import play.api.mvc.{AnyContent, MessagesControllerComponents, Request}
import play.twirl.api.Html
import play.api.i18n.Messages.Implicits._
import play.api.Play.current


class PageFourController @Inject() (refNumAction: RefNumAction,
                                    cc: MessagesControllerComponents,
                                    part4: views.html.part4)
  extends ForDataCapturePage[PageFour] (refNumAction, cc) {
  val format = p4f
  val emptyForm = pageFourForm
  val pageNumber: Int = 4

  def template(form: Form[PageFour], summary: Summary)(implicit request: RefNumRequest[AnyContent]): Html = {
    part4(form, summary)
  }
}
