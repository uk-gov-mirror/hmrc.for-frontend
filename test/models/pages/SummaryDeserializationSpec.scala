package models.pages

import connectors.{Document, Page}
import models.RentLengthTypeQuarterly
import models.serviceContracts.submissions.OccupierTypeIndividuals
import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class SummaryDeserializationSpec extends FlatSpec with Matchers with OptionValues {

  val pages: Seq[Page] = Seq(
    Page(3, Map(
      "propertyType" -> Seq("hotel"),
      "occupierType" -> Seq("individuals"),
      "firstOccupationDate.month" -> Seq("02"),
      "firstOccupationDate.year" -> Seq("2018"),
      "mainOccupierName" -> Seq("John John"),
      "propertyOwnedByYou" -> Seq("true")
    )),

    Page(9, Map(
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
    ))
  )

  val doc: Document = Document(
    referenceNumber = "1111",
    journeyStarted = DateTime.now(),
    pages = pages,
    address = None,
    saveForLaterPassword = None,
    journeyResumptions = Seq()
  )

  "Summary builder" should "deserialize document with redundant fields " in {
     val summaryBuilder: SummaryBuilder = SummaryBuilder
     val summary = summaryBuilder.build(doc)

    summary.theProperty shouldBe defined
    summary.theProperty.value.propertyType shouldBe "hotel"
    summary.theProperty.value.occupierType shouldBe OccupierTypeIndividuals


    summary.rent shouldBe defined
    summary.rent.value.totalRent.period shouldBe RentLengthTypeQuarterly
    summary.rent.value.totalRent.amount shouldBe BigDecimal("10000")

   }





}
