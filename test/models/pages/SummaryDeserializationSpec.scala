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

package models.pages

import connectors.{Document, Page}
import models.RentLengthTypeQuarterly
import models.serviceContracts.submissions.OccupierTypeIndividuals
import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class SummaryDeserializationSpec extends FlatSpec with Matchers with OptionValues {


  val page9Map = Map(
    "totalRent.annualRentExcludingVat" -> Seq("10000"),
    "totalRent.rentLengthType" -> Seq("quarterly"),
    "totalRent.SomethingForTest" -> Seq("testing value"),
    "rentBecomePayable.day" -> Seq("20"),
    "rentBecomePayable.month" -> Seq("12"),
    "rentBecomePayable.year" -> Seq("2018"),
    "rentActuallyAgreed.day" -> Seq("20"),
    "rentActuallyAgreed.month" -> Seq("12"),
    "rentActuallyAgreed.year" -> Seq("2018"),
    "negotiatingNewRent" -> Seq("false"),
    "rentBasedOn" -> Seq("other"),
    "rentBasedOnDetails" -> Seq("vvsdfsd sdf")
  )

  val page9 = Page(9, page9Map)

  val page3 = Page(3, Map(
    "propertyType" -> Seq("hotel"),
    "occupierType" -> Seq("individuals"),
    "firstOccupationDate.month" -> Seq("02"),
    "firstOccupationDate.year" -> Seq("2018"),
    "mainOccupierName" -> Seq("John John"),
    "propertyOwnedByYou" -> Seq("true")
  ))

  val pages: Seq[Page] = Seq(page3,page9)

  val doc: Document = Document(
    referenceNumber = "1111",
    journeyStarted = DateTime.now(),
    pages = pages,
    address = None,
    saveForLaterPassword = None,
    journeyResumptions = Seq()
  )

  "Summary builder" should "deserialize document with redundant fields " in {
    val aDocument = Document(
      referenceNumber = "1111",
      journeyStarted = DateTime.now(),
      pages = Seq(page3, Page(9, page9Map + ("totalRent.annualRentExcludingVat2" -> Seq("10000")))),
      address = None,
      saveForLaterPassword = None,
      journeyResumptions = Seq()
    )

     val summaryBuilder: SummaryBuilder = SummaryBuilder
     val summary = summaryBuilder.build(aDocument)

    summary.theProperty shouldBe defined
    summary.theProperty.value.propertyType shouldBe "hotel"
    summary.theProperty.value.occupierType shouldBe OccupierTypeIndividuals


    summary.rent shouldBe defined
    summary.rent.value.totalRent.amount shouldBe BigDecimal("10000")

   }

  "Summary builder" should "not deserialize data in old format" in {
    val summaryBuilder: SummaryBuilder = SummaryBuilder
    val summary = summaryBuilder.build(doc)

    summary.theProperty shouldBe defined
    summary.theProperty.value.propertyType shouldBe "hotel"
    summary.theProperty.value.occupierType shouldBe OccupierTypeIndividuals

    summary.rent shouldBe None

  }





}
