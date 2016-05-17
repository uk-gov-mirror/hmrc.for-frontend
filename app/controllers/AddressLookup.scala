/*
 * Copyright 2016 HM Revenue & Customs
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
import connectors.HODConnector._
import play.api.libs.json.{JsArray, JsValue, Json}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.{BadRequestException, HeaderCarrier}

import scala.concurrent.Future

object AddressLookup extends FrontendController {
  def getAddress(postcode: String) = RefNumAction.async { implicit request =>
    AddressLookupConnector.getAddress(postcode, hc) map { Ok(_) } recoverWith {
      case b: BadRequestException => BadRequest
    }
  }
}

object AddressLookupConnector {
  lazy val serviceUrl = baseUrl("address-lookup")
  val http = playconfig.WSHttp

  def getAddress(postcode: String, hc: HeaderCarrier): Future[JsValue] = {
    implicit val h = hc.withExtraHeaders("X-Hmrc-Origin" -> "VOA-FOR")
    //http.GET[JsValue](serviceUrl + s"/v1/uk/addresses.json?postcode=$postcode")
    Future.successful(
      Json.parse("""[{"id":"GB10033548251","address":{"lines":["Basement Lg7, Admiralty Arch","The Mall"],"town":"London"
                   |,"postcode":"SW1A 1AA","country":{"code":"UK","name":"United Kingdom"}},"language":"en"},{"id":"GB10033544614"
                   |,"address":{"lines":["Buckingham Palace"],"town":"London","postcode":"SW1A 1AA","country":{"code":"UK"
                   |,"name":"United Kingdom"}},"language":"en"},{"id":"GB10033562298","address":{"lines":["East, Buckingham
                   | Palace","Buckingham Gate"],"town":"London","postcode":"SW1A 1AA","country":{"code":"UK","name":"United
                   | Kingdom"}},"language":"en"},{"id":"GB10033598924","address":{"lines":["Royal Guard Room","The Royal
                   | Mews"],"town":"London","postcode":"SW1A 1AA","country":{"code":"UK","name":"United Kingdom"}},"language"
                   |:"en"},{"id":"GB10033562299","address":{"lines":["West, Buckingham Palace","Buckingham Gate"],"town"
                   |:"London","postcode":"SW1A 1AA","country":{"code":"UK","name":"United Kingdom"}},"language":"en"}]""".stripMargin.replaceAll("\n", ""))
    )
  }

}
