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

package form

import org.scalatest.{FlatSpec, Matchers}
import play.api.data.FormError

class PageOneFormMappingSpec extends FlatSpec with Matchers {
  import TestData._
  import form.PageOneForm._
  import utils.MappingSpecs._
  
  "page one mapping" should "error if the isAddressCorrect field is missing" in {
    val formData: Map[String, String] = Map()
    val boundForm = pageOneForm.bind(formData)

    boundForm.hasErrors should be(true)
    boundForm.errors.size should be(1)
    val err1 = boundForm.errors(0)
    err1.key should be(errorKey.isAddressCorrect)
    err1.messages should contain(Errors.booleanMissing)
  }


  it should "validate the address fields following the standard address convention for this application" in {
    val formData: Map[String, String] = Map(errorKey.isAddressCorrect -> "false",
      errorKey.addressBuildingName -> "buildingNameText",
      errorKey.addressPostcode -> "AA11 1AA",
      "address.street1" -> "street1",
      "address.street2" -> "street2")

    validateAddress(pageOneForm, formData, "address")
  }

  "page one mapping" should "be okay if isAddressCorrect field is true" in {
    val formData: Map[String, String] = Map(errorKey.isAddressCorrect -> "true")
    val boundForm = pageOneForm.bind(formData).convertGlobalToFieldErrors()

    boundForm.hasErrors should be(false)

    boundForm.value.map { pageOneData =>
      pageOneData.isAddressCorrect should be(true)
    }
  }

  "page one mapping" should "map to fully populated data object if all form fields are present and isAddresCorrect is false" in {
    val formData: Map[String, String] = Map(
      errorKey.isAddressCorrect -> "false",
      errorKey.addressBuildingName -> "buildingNameText",
      errorKey.addressPostcode -> "AA11 1AA",
      "address.street1" -> "street1",
      "address.street2" -> "street2")
    val boundForm = pageOneForm.bind(formData).convertGlobalToFieldErrors()

    boundForm.hasErrors should be(false)

    boundForm.value.isDefined should be(true)

    val pageOneData = boundForm.value.get

    pageOneData.isAddressCorrect should be(false)
    pageOneData.address.isDefined should be(true)
    pageOneData.address.get.buildingNameNumber should be("buildingNameText")
    pageOneData.address.get.street1 should be(Some("street1"))
    pageOneData.address.get.street2 should be(Some("street2"))
    pageOneData.address.get.postcode should be("AA11 1AA")
  }

  object TestData {
    val errorKey = new {
    val isAddressCorrect: String = "isAddressCorrect"
    val addressBuildingName: String = "address.buildingNameNumber"
    val addressPostcode: String = "address.postcode"
  }

  val formErrors = new {
    val required = new {
      val isAddressCorrect = FormError(errorKey.isAddressCorrect, Errors.booleanMissing)
    }
  }
  
  }
}
