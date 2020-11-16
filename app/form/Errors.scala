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

package form

object Errors {
  val required = "error.required"
  val emailMismatch = "email.mismatch"
  val addressRequired  = "address.required"
  val contactDetailsMissing = "error.contact.details.missing"
  val contactPhoneRequired = "error.contact.phone.required"
  val contactEmailRequired = "error.contact.email.required"
  val alternativeAddressMissing = "error.alternative.address.missing"
  val alternativeContactMissing  = "error.alternative.contact.missing"
  val booleanMissing = "error.boolean_missing"
  val contactMissing = "error.contact.missing"
  val landlordConnectionTypeMissing = "error.landlord_connection_type_missing"
  val lastReviewalIntervalTypeMissing = "error.rent_review_interval_type_missing"
  val lastReviewalDateMissing = "error.last_review_date_missing"
  val addressBuildingNameNumberRequired = "error.address.buildingName.required"
  val addressTownCityRequired = "error.address.townCity.required"
  val addressPostcodeRequired = "error.address.postcode.required"

  val rentReviewDetailsRequired = "error.rentReviewDetails.required"
  val rentReviewFrequencyRequired = "error.rentReviewFrequency.required"
  val rentCanBeReducedOnReviewRequired = "error.canRentBeReducedOnReview.required"
  val isRentResultOfReviewRequired = "error.isRentResultOfReview.required"
  val rentWasAgreedBetweenRequired = "error.rentWasAgreedBetween.required"
  val rentFixedByRequired = "error.rentWasFixedBy.required"

  val rentBaseTypeRequired = "error.rentBaseOn.required"

  val leaseAgreementTypeRequired = "error.leaseType.required"
  val leaseAgreementOpenEndedRequired = "error.leaseOpenEnded.required"
  val leaseAgreementBreakClauseRequired = "error.leaseHasBreakClause.required"
  val leaseAgreementIsSteppedRequired = "error.steppedRent.required"


  val includesLivingAccommodationRequired = "error.includesLivingAccommodation.required"
  val isRentPaidForPartRequired = "error.isRentPaidForPart.required"
  val anyOtherBusinessPropertyRequired = "error.anyOtherBusinessProperty.required"
  val rentBasedOnLandOnlyRequired = "error.rentBasedOnLandOnly.required"
  val rentBasedOnEmptyBuildingRequired = "error.rentBasedOnEmptyBuilding.required"
  val includesParkingRequired = "error.rentIncludesParking.required"
  val tenantPaysForParkingRequired = "error.tenantPaysForParking.required"


  val rentFreePeriodRequired = "error.rentFreePeriod.required"
  val paidCapitalSumRequired = "error.paidCapitalSum.required"
  val receivedCapitalSumRequired = "error.receivedCapitalSum.required"
  val responsibleOutsideRepairsRequired = "error.responsibleOutsideRepairs.required"
  val responsibleInsideRepairsRequired = "error.responsibleInsideRepairs.required"


  val waterChargeResponsibleTypesMissing = "error.water.charges.responsible.types.missing"
  val bigDecimalNegative = "error.BigDecimal_negative"
  val declaration = "error.declaration"
  val maxLength = "error.maxLength"
  val invalidPostcode = "error.invalid_postcode"
  val invalidPostcodeOnLetter = "error.invalid_postcode_as_on_letter"
  val invalidPhone = "error.invalid_phone"
  val invalidCurrency = "error.invalid_currency"
  val dateBefore1900 = "error.date_before_1900"
  val invalidDate = "error.invalid_date"
  val invalidNumber = "error.invalid_number"
  val dateMustBeInPast = "error.date_must_be_in_past"
  val number = "error.number"
  val invalidDurationMonths = "error.duration.months"
  val invalidDurationYears = "error.duration.years"
  val noValueSelected = "error.no_value_selected"
  val invalidRefNum = "error.invalid_refnum"
  val tooManySteppedRents = "error.too_many_stepped_rents"
  val tooManyServices = "error.too_many_services"
  val tooManyAlterations = "error.too_many_alterations"
  val tooManySublets = "error.too_many_sublets"
  val parkingRequired = "error.required.parking"
  val maxCurrencyAmountExceeded = "error.maxCurrencyAmountExceeded"
  val toDateIsAfterFromDate = "error.writtenAgreement.steppedDetails.stepTo.day"
  val overlappingDates = "error.steppedDetails.overlappingDates"
  val isConnectedError = "error.isRelated"

  val userTypeRequired = "error.userType.required"
  val contactTypeRequired = "error.contactType.required"
  val occupierTypeRequired = "error.occupierType.required"
  val propertyOwnedByYouRequired = "error.propertyOwnedByYou.required"
  val propertyRentedByYouRequired = "error.propertyRentedByYou.required"
  val propertyIsSublet = "error.propertyIsSublet.required"
  val subletTypeRequired = "error.subletType.required"
  val LandlordConnectionTypeRequired = "error.LandlordConnectionType.required"

}
