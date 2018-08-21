/*
 * Copyright 2018 HM Revenue & Customs
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

package connectors

import config.ForConfig
import org.joda.time.LocalDate
import play.api.i18n.{Lang, Messages}
import play.api.libs.json._
import uk.gov.hmrc.play.config.ServicesConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

object EmailConnector extends ServicesConfig {

  lazy val emailUrl = baseUrl("email")
  val http = ForConfig.http

  def sendEmail(refNumber: String, postcode: String, email: Option[String], expiryDate: LocalDate)(implicit hc: HeaderCarrier, messages: Messages) = {
    email.map { e =>
      val formattedExpiryDate = s"${expiryDate.getDayOfMonth} ${Messages(s"month.${expiryDate.monthOfYear.getAsText}")} ${expiryDate.getYear}"
      val json = Json.obj(
        "to" -> JsArray(Seq(JsString(e))),
        "templateId" -> JsString("rald_alert"),
        "parameters" -> JsObject(Seq(
          "referenceNumber" -> JsString(s"""${Messages("saveForLater.refNum")}: $refNumber"""),
          "postcode" -> JsString(s"""${Messages("saveForLater.postcode")}: $postcode"""),
          "expiryDate" -> JsString(s"""${Messages("saveForLater.paragraph")} $formattedExpiryDate""")
        )),
        "force" -> JsBoolean(false)
      )
      http.POST(s"$emailUrl/send-templated-email/", json).map( _ => ())
    } getOrElse Future.successful(())
  }
}
