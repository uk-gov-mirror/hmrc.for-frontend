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

package models.pages

import connectors.Document
import form.PageZeroForm.pageZeroForm
import form.PreviouslyConnectedForm.formMapping
import form.NotConnectedPropertyForm.form

import play.api.data.Form

trait NotConnectedSummaryBuilder {
  def build(doc: Document): Summary
}

object NotConnectedSummaryBuilder extends NotConnectedSummaryBuilder {

  def build(doc: Document): Summary = {
    val p0 = findPage(doc, 0, pageZeroForm) //Are you still connected? (page 0)
    val pSummary = findPage(doc, 15, formMapping) //Have you ever been connected? (previously-connected)
    val rSummary = findPage(doc, 16, form)// Remove connection to property; Full name, Address, Phone, Email (not-connected)

    NotConnectedSummary(p0, pSummary, rSummary)
  }

  private def findPage[T](doc: Document, pageNumber: Int, form: Form[T]) = doc.page(pageNumber) flatMap { p =>
    form.bindFromRequest(p.fields).value
  }
}
