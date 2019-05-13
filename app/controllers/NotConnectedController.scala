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

import models.pages.Summary
import models.serviceContracts.submissions.Address
import org.joda.time.DateTime
import play.api.mvc.Action
import uk.gov.hmrc.play.frontend.controller.FrontendController
import play.api.i18n.Messages.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys, Upstream4xxResponse}
import play.api.Play.current

object NotConnectedController extends FrontendController {


  def onPageView = Action { implicit request =>

    val summary = Summary (
      referenceNumber = "10643313719",
      journeyStarted = DateTime.now(),
      propertyAddress = None,
      customerDetails = None,
      theProperty = None,
      sublet = None,
      landlord = None,
      lease = None,
      rentReviews = None,
      rentAgreement = None,
      rent = None,
      rentIncludes = None,
      incentives = None,
      responsibilities = None,
      alterations = None,
      otherFactors = None,
      address = Option(Address("Unit 7", Option("Maltings Industrial Estate"), Option("Charmley"), "CH1 1AA"))
    )



    Ok(views.html.notConnected(Some(summary)))

  }



}

