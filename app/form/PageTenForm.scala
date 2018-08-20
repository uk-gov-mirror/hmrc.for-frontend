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

import form.DateMappings._
import form.MappingSupport.{mandatoryBoolean, _}
import models.serviceContracts.submissions.{Parking, WhatRentIncludes}
import play.api.data.{Mapping, Form}
import play.api.data.Forms.{mapping, nonEmptyText}
import uk.gov.voa.play.form.ConditionalMappings._

import PageTenForm._

object PageTenForm {

  object Keys {
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
    Keys.partRent -> mandatoryBoolean,
    Keys.otherProperty -> mandatoryBoolean,
    Keys.livingAccomodation -> mandatoryBoolean,
    Keys.landOnly -> mandatoryBoolean,
    Keys.shellUnit -> mandatoryBoolean,
    Keys.rentDetails -> mandatoryIfAnyAreTrue(
      Seq(Keys.shellUnit, Keys.landOnly, Keys.livingAccomodation, Keys.otherProperty, Keys.partRent),
      nonEmptyText(maxLength = 249), showNestedErrors = false
    ),
    "parking" -> ParkingMapping.parkingMapping("parking")
  )(WhatRentIncludes.apply)(WhatRentIncludes.unapply)

  val pageTenForm = Form(pageTenMapping)
}

object ParkingMapping {

  private def rentIncludeParkingMapping(prefix: String) = mandatoryIfTrue(
    prefix + Keys.rentIncludeParking, parkingDetailsMapping(s"$prefix${Keys.rentIncludeParkingDetails}")
  )

  private def rentSeparateParkingDetailsMapping(prefix: String) = mandatoryIfTrue(
    prefix + Keys.rentSeparateParking, parkingDetailsMapping(s"$prefix${Keys.rentSeparateParkingDetails}")
  )

  def parkingMapping(prefix: String): Mapping[Parking] = {
    val pfx = prefix + "."
    mapping(
      (Keys.rentIncludeParking, mandatoryBoolean),
      (Keys.rentIncludeParkingDetails, rentIncludeParkingMapping(pfx)),
      (Keys.rentSeparateParking, mandatoryBoolean),
      (Keys.rentSeparateParkingDetails, rentSeparateParkingDetailsMapping(pfx)),
      (Keys.annualSeparateParking, mandatoryIfTrue(pfx + Keys.rentSeparateParking, currency)),
      (Keys.annualSeparateParkingDate, mandatoryIfTrue(
        pfx + Keys.rentSeparateParking, monthYearRoughDateMapping(pfx + Keys.annualSeparateParkingDate)))
    )(Parking.apply)(Parking.unapply)
  }
}
