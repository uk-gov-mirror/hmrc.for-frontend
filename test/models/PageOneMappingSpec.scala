/*
 * Copyright 2017 HM Revenue & Customs
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

package models

import models.serviceContracts.submissions.Address
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.{JsSuccess, JsResult, Json}

class PageOneMappingSpec extends FlatSpec with Matchers {

  val json1 = """{"isAddressCorrect":true}"""
  val data1 = PropertyAddress(isAddressCorrect = true, address = None)

  val json2 = """{"isAddressCorrect":false,"address":{"buildingNameNumber":"Some House","street1":"Some Street","street2":"Some City","postcode":"AA11 1AA"}}"""
  val data2 = PropertyAddress(isAddressCorrect = false, address = Some(Address("Some House", Some("Some Street"),Some("Some City"),"AA11 1AA")))

  val json3 = """{"isAddressCorrect":false,"address":{"buildingNameNumber":"Some House","street2":"Some City","postcode":"AA11 1AA"}}"""
  val data3 = PropertyAddress(isAddressCorrect = false, address = Some(Address("Some House", None, Some("Some City"), "AA11 1AA")))

  val json4 = """{"isAddressCorrect":false,"address":{"buildingNameNumber":"Some House","street1":"Some Street","street2":"Some City","postcode":"AA11 1AA"}}"""
  val data4 = PropertyAddress(isAddressCorrect = false, address = Some(Address("Some House", Some("Some Street"), Some("Some City"),"AA11 1AA")))
 
  def toJson(data:PropertyAddress):String = {
    val json = Json.toJson(data).toString
    json
  }

  def fromJson(json:String):JsResult[PropertyAddress]= {
    Json.fromJson[PropertyAddress](Json.parse(json))
  }

  "PropertyAddress with isAddressCorrect true" should "map to expected json" in {
    toJson(data1) should be (json1)
  }

  " isAddressCorrect:true json" should "maps into to PropertyAddress" in {
    fromJson(json1) should be(JsSuccess(data1))
  }

  "PropertyAddress with a fully filled in address" should "create a fully filled PropertyAddress" in {
    fromJson(json2) should be(JsSuccess(data2))
  }
  
  "PropertyAddress with isAddressCorrect false and full address" should "map to expected json" in {
    val result = toJson(data2)
    result should be(json2)
  }
  
  "PropertyAddress with isAddressCorrect false and address missing street1" should "map to expected json" in {
    toJson(data3) should be(json3)
  }

  "PropertyAddress with an address missing street1" should "create a PropertyAddress without a street1" in {
    fromJson(json3) should be(JsSuccess(data3))
  }
  
  "PropertyAddress with isAddressCorrect false and address missing street2" should "map to expected json" in {
    toJson(data4) should be(json4)
  }

  "PropertyAddress with an address missing street1" should "create a PropertyAddress without a street2" in {
    fromJson(json4) should be(JsSuccess(data4))
  }

}
