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

package form

import form.DateMappings._
import form.MappingSupport.{mandatoryBoolean, _}
import models.serviceContracts.submissions.{Parking, WhatRentIncludes}
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import uk.gov.voa.play.form.ConditionalMappings._


object PageTenForm {

  object keys {
    val partRent = "partRent"
    val partRentDetails = "partRentDetails"
    val otherProperty = "otherProperty"
    val otherPropertyDetails = "otherPropertyDetails"
    val livingAccomodation = "livingAccomodation"
    val livingAccommodationDetails = "livingAccomodationDetails"
    val landOnly = "landOnly"
    val landOnlyDetails = "landOnlyDetails"
    val shellUnit = "shellUnit"
    val shellUnitDetails = "shellUnitDetails"
    val rentDetails = "rentDetails"
    val rentIncludeParking = "rentIncludeParking"
    val rentIncludeParkingDetails = "rentIncludeParkingDetails"
    val rentSeparateParkingDetails = "rentSeparateParkingDetails"
    val rentSeparateParking = "rentSeparateParking"
    val annualSeparateParking = "annualSeparateParking"
    val annualSeparateParkingDate = "annualSeparateParkingDate"
  }

  val pageTenMapping = mapping(
    keys.partRent -> mandatoryBoolean,
    keys.otherProperty -> mandatoryBoolean,
    keys.livingAccomodation -> mandatoryBoolean,
    keys.landOnly -> mandatoryBoolean,
    keys.shellUnit -> mandatoryBoolean,
    keys.rentDetails -> mandatoryIfAnyAreTrue(
      Seq(keys.shellUnit, keys.landOnly, keys.livingAccomodation, keys.otherProperty, keys.partRent),
      nonEmptyText(maxLength = 249), showNestedErrors = false
    ),
    "parking" -> ParkingMapping.parkingMapping("parking")
  )(WhatRentIncludes.apply)(WhatRentIncludes.unapply)

  val pageTenForm = Form(pageTenMapping)
}

object ParkingMapping {
  import PageTenForm._

  def rentIncludeParkingMapping(prefix: String) = mandatoryIfTrue(
    prefix + keys.rentIncludeParking, parkingDetailsMapping(s"$prefix${keys.rentIncludeParkingDetails}")
  )

  def rentSeparateParkingDetailsMapping(prefix: String) = mandatoryIfTrue(
    prefix + keys.rentSeparateParking, parkingDetailsMapping(s"$prefix${keys.rentSeparateParkingDetails}")
  )

  def parkingMapping(prefix: String) = {
    val pfx = prefix + "."
    mapping(
      (keys.rentIncludeParking, mandatoryBoolean),
      (keys.rentIncludeParkingDetails, rentIncludeParkingMapping(pfx)),
      (keys.rentSeparateParking, mandatoryBoolean),
      (keys.rentSeparateParkingDetails, rentSeparateParkingDetailsMapping(pfx)),
      (keys.annualSeparateParking, mandatoryIfTrue(pfx + keys.rentSeparateParking, currency)),
      (keys.annualSeparateParkingDate, mandatoryIfTrue(
        pfx + keys.rentSeparateParking, monthYearRoughDateMapping(pfx + keys.annualSeparateParkingDate)))
    )(Parking.apply)(Parking.unapply)
  }
}
