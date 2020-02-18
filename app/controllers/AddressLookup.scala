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
import connectors.ForHttp
import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AddressLookup @Inject()(cc: ControllerComponents, refNumAction: RefNumAction, http: ForHttp, serviceConfig: ServicesConfig) extends FrontendController(cc) {

  val serviceUrl = serviceConfig.baseUrl("address-lookup")

  def getAddress(postcode: String) = refNumAction.async { implicit request =>
    getAddress(postcode, hc) map { Ok(_) } recoverWith {
      case b: BadRequestException => BadRequest
    }
  }
  def getAddress(postcode: String, hc: HeaderCarrier): Future[JsValue] = {
    implicit val h = hc.withExtraHeaders("X-Hmrc-Origin" -> "VOA-FOR")
    http.GET[JsValue](serviceUrl + s"/v1/uk/addresses.json?postcode=$postcode")
  }

}
