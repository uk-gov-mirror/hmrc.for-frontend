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

package utils

import form.PageOneForm._
import models._
import models.pages._
import models.serviceContracts.submissions._
import org.joda.time.{DateTime, LocalDate}

object SummaryBuilder {
	def apply(page1: Option[PropertyAddress] = None,
                             page2: Option[CustomerDetails] = None,
                             page3: Option[PageThree] = None,
                             page4: Option[PageFour] = None,
                             page5: Option[PageFive] = None,
                             page6: Option[PageSix] = None,
                             page7: Option[PageSeven] = None,
                             page8: Option[RentAgreement] = None,
                             page9: Option[PageNine] = None,
                             page10: Option[WhatRentIncludes] = None,
                             page11: Option[IncentivesAndPayments] = None,
                             page12: Option[PageTwelve] = None,
                             page13: Option[PropertyAlterations] = None,
                             page14: Option[OtherFactors] = None) = {
    Summary("", DateTime.now, page1, page2, page3, page4, page5, page6, page7, page8, page9, page10, page11, page12, page13, page14)
  }

  lazy val completeShortPathJourney = SummaryBuilder(Some(pageOneData), Some(pageTwoData), Some(propertyOwned), Some(propertyNotSublet))
  lazy val completeFullPathJourney = SummaryBuilder(
   Some(pageOneData), Some(pageTwoData), Some(propertyRented), Some(propertyIsSublet), Some(pageFiveData), Some(pageSixData), 
   Some(pageSevenData), Some(pageEightData), Some(pageNineData), Some(pageTenData), Some(pageElevenData), Some(pageTwelveData), 
   Some(pageThirteenData), Some(pageFourteenData)
  )
  lazy val incompletePageOneJourney = SummaryBuilder()
  lazy val incompletePageFourJourney = SummaryBuilder(Some(pageOneData), Some(pageTwoData), Some(propertyOwned))
  lazy val incompletePageFourteenJourney = completeFullPathJourney.copy(otherFactors = None)
  lazy val completeShortPathJourneyWithEditedPageOne = SummaryBuilder(editedPageOneData, Some(pageTwoData), Some(propertyOwned), Some(propertyNotSublet))

  private lazy val editedPageOneData = basePageOneForm.bind(editedPageOneForm).value
  private lazy val editedPageOneForm = Map("isAddressCorrect" -> "false", "address.buildingNameNumber" -> "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "address.postcode" -> "BN12 4AX")
  private lazy val pageOneData = PropertyAddress(true, None)

  private lazy val pageTwoData = CustomerDetails("name", UserTypeOwner, ContactTypePhone, ContactDetails(None, None, None))

  private lazy val propertyOwned = PageThree(
    propertyType = "property type",
    occupierType = OccupierTypeCompany,
    occupierCompanyName = Some("Some Company"),
    occupierCompanyContact = Some("Some Company Contact"),
    firstOccupationDate = Some(RoughDate(Some(28), Some(2), 2015)),
    None,
    propertyOwnedByYou = true,
    propertyRentedByYou = None,
    noRentDetails = None
  )

  private lazy val propertyRented = PageThree(
    propertyType = "property type",
    occupierType = OccupierTypeCompany,
    occupierCompanyName = Some("Some Company"),
    occupierCompanyContact = Some("Some Company Contact"),
    firstOccupationDate = Some(RoughDate(Some(28), Some(2), 2015)),
    None,
    propertyOwnedByYou = false,
    propertyRentedByYou = Some(true),
    noRentDetails = None
  )

  private lazy val propertyNotOwnedOrRented = PageThree(
    propertyType = "property type",
    occupierType = OccupierTypeCompany,
    occupierCompanyName = Some("Some Company"),
    occupierCompanyContact = Some("Some Company Contact"),
    firstOccupationDate = Some(RoughDate(Some(28), Some(2), 2015)),
    None,
    propertyOwnedByYou = false,
    propertyRentedByYou = Some(false),
    noRentDetails = None
  )

  private lazy val propertyNotSublet = PageFour(
    false, List.empty
  )

  private lazy val propertyIsSublet = PageFour(
    true, List(SubletDetails(
      "Something",Address("Street address", None, Some("City"), "Postcode"), "Description", "Reason", BigDecimal(1.33), RoughDate(None, Some(12), 1980))
    )
  )

  private lazy val pageFiveData = PageFive(
    false, "name", Some(Address("line1", None, Some("city"), "postcode")), LandlordConnectionTypeNone, None
  )
  private lazy val pageSixData = PageSix(
    LeaseAgreementTypesLeaseTenancy, Some(WrittenAgreement(RoughDate(None, None, 1), false, None, false, None, false, Nil)), VerbalAgreement()
  )
  private lazy val pageSixNoVerbal = PageSix(
    LeaseAgreementTypesLeaseTenancy, Some(WrittenAgreement(RoughDate(None, None, 1), false, None, false, None, false, Nil)), VerbalAgreement()
  )
  private lazy val leaseAgreementTenancy = PageSix(
    LeaseAgreementTypesLeaseTenancy, Some(WrittenAgreement(RoughDate(None, None, 1), false, None, false, None, false, Nil)), VerbalAgreement()
  )
  private lazy val pageSixVerbal = PageSix(
    LeaseAgreementTypesVerbal, None, VerbalAgreement(Some(RoughDate(None, None, 1)), Some(false))
  )
  private lazy val pageSevenData = PageSeven(false, None)
  private lazy val pageEightData = RentAgreement(true, None, RentSetByTypeNewLease)
  private lazy val hasNoRentReviews = PageSeven(false, None)
  private lazy val hasRentReviews = PageSeven(true, None)
  private lazy val pageNineData = PageNine(
    AnnualRent(RentLengthTypeMonthly, 8.99), rentBecomePayable = new LocalDate(2010, 2, 27), rentActuallyAgreed = new LocalDate(2005, 4, 2), 
    negotiatingNewRent = true, rentBasis = RentBaseTypeOpenMarket, None
  )
  private lazy val pageTenData = WhatRentIncludes(false, false, false, false, false, None, Parking(false, None, false, None, None, None))
  private lazy val pageElevenData = IncentivesAndPayments(false, None, false, None, false, None)
  private lazy val pageTwelveData = PageTwelve(
    ResponsibleLandlord, ResponsibleLandlord, ResponsibleLandlord, false, None, false, None, false, Nil
  )
  private lazy val pageThirteenData = PropertyAlterations(false, List.empty, None)
  private lazy val pageFourteenData = OtherFactors(false, None)
}
