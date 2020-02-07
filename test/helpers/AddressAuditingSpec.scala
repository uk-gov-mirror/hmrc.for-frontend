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

package helpers

import models.{LookupServiceAddress, RoughDate}
import models.pages.{PageFive, PageFour, SubletDetails, Summary}
import models.serviceContracts.submissions.{Address, AddressConnectionTypeYesChangeAddress, LandlordConnectionTypeNone}
import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}
import play.api.test.FakeRequest
import playconfig.Audit
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult.Disabled

import scala.concurrent.Future

class AddressAuditingSpec extends FlatSpec with Matchers {
  import TestData._

  behavior of "Address Auditing"

  it should "send a manualAddressSubmitted audit when the user has ignored the postcode lookup and entered the landlord's address manually" in {
    val s = summaryWithLandlordAddress(None, Some(manual))
    TestAddressAuditing(s, FakeRequest())

    StubAuditer.mustHaveSentAudit("manualAddressSubmitted",
      Map(
        "submittedLine1" -> manual.buildingNameNumber,
        "submittedLine2" -> manual.street1.getOrElse(""),
        "submittedPostcode" -> manual.postcode
      )
    )
  }

  it should "send a postcodeAddressSubmitted audit when the user has not modified the address returned by the postcode lookup" in {
    val s = summaryWithLandlordAddress(Some(fromPostcodeLookup), Some(unchanged))
    TestAddressAuditing(s, FakeRequest())

    StubAuditer.mustHaveSentAudit("postcodeAddressSubmitted",
      Map(
        "submittedLine1" -> fromPostcodeLookup.buildingNameNumber,
        "submittedLine2" -> fromPostcodeLookup.street1.getOrElse(""),
        "submittedPostcode" -> fromPostcodeLookup.postcode,
        "submittedUPRN" -> fromPostcodeLookup.uprn
      )
    )
  }

  it should "send a postcodeAddressModifiedSubmitted audit when the user has modified one line of the landlord address returned by the postcode lookup" in {
    val s = summaryWithLandlordAddress(Some(fromPostcodeLookup), Some(oneLineChanged))
    TestAddressAuditing(s, FakeRequest())

    StubAuditer.mustHaveSentAudit("postcodeAddressModifiedSubmitted",
      Map(
        "submittedLine1" -> oneLineChanged.buildingNameNumber,
        "submittedLine2" -> oneLineChanged.street1.getOrElse(""),
        "submittedPostcode" -> oneLineChanged.postcode,
        "originalLine1" -> fromPostcodeLookup.buildingNameNumber,
        "originalLine2" -> fromPostcodeLookup.street1.getOrElse(""),
        "originalPostcode" -> fromPostcodeLookup.postcode,
        "originalUPRN" -> fromPostcodeLookup.uprn
      )
    )
  }

  it should "send a manualAddressSubmitted audit when the user has modified at least two lines of the landlord address returned by the postcode lookup" in {
    val s = summaryWithLandlordAddress(
      Some(fromPostcodeLookup),
      Some(twoLinesChanged)
    )
    TestAddressAuditing(s, FakeRequest())

    StubAuditer.mustHaveSentAudit("manualAddressSubmitted",
      Map(
        "submittedLine1" -> twoLinesChanged.buildingNameNumber,
        "submittedLine2" -> twoLinesChanged.street1.getOrElse(""),
        "submittedPostcode" -> twoLinesChanged.postcode
      )
    )
  }

  it should "send a internationalAddressSubmitted audit when the landlord address does not have a valid UK postcode" in {
    val s = summaryWithLandlordAddress(None, Some(overseas))
    TestAddressAuditing(s, FakeRequest())

    StubAuditer.mustHaveSentAudit("internationalAddressSubmitted",
      Map(
        "submittedLine1" -> overseas.buildingNameNumber,
        "submittedLine2" -> overseas.street1.getOrElse(""),
        "submittedPostcode" -> overseas.postcode
      )
    )
  }

  it should "send a manualAddressSubmitted audit when the user submits a corrected property address" in {
    val s = summaryWithPropertyAddress(Some(propertyAddress), Some(oneLineChanged))
    TestAddressAuditing(s, FakeRequest())

    StubAuditer.mustHaveSentAudit("manualAddressSubmitted",
      Map(
        "submittedLine1" -> oneLineChanged.buildingNameNumber,
        "submittedLine2" -> oneLineChanged.street1.getOrElse(""),
        "submittedPostcode" -> oneLineChanged.postcode
      )
    )
  }

  it should "send a manualAddressSubmitted audit when the user submits a corrected sublet address" in {
    val s = summaryWithSubletAddress(Some(propertyAddress), oneLineChanged)
    TestAddressAuditing(s, FakeRequest())

    StubAuditer.mustHaveSentAudit("manualAddressSubmitted",
      Map(
        "submittedLine1" -> oneLineChanged.buildingNameNumber,
        "submittedLine2" -> oneLineChanged.street1.getOrElse(""),
        "submittedPostcode" -> oneLineChanged.postcode
      )
    )
  }

  private def summaryWithPropertyAddress(voaAddress: Option[Address], corrected: Option[Address]): Summary = {
    Summary("1234567890", DateTime.now,
      Some(AddressConnectionTypeYesChangeAddress), corrected,
      None, None, None, None, None, None, None, None, None, None, None, None, None,
      voaAddress, Nil
    )
  }

  private def summaryWithSubletAddress(voaAddress: Option[Address], submitted: Address): Summary = {
    Summary("123467890", DateTime.now, None, None, None, None,
      Some(PageFour(true, List(SubletDetails("Mr Tenant", submitted, "Something", "Stuff", BigDecimal(0.01), RoughDate(None, Some(1), 2011))))),
      None, None, None, None, None, None, None, None, None, None, voaAddress, Nil)
  }

  private def summaryWithLandlordAddress(original: Option[LookupServiceAddress], submitted: Option[Address]): Summary = {
    Summary("1234567890", DateTime.now, None, None, None, None, None,
      Some(PageFive(false, Some("Mr Landlord"), original, submitted, LandlordConnectionTypeNone, None)),
      None, None, None, None, None, None, None, None, None, None, Nil)
  }
}

object TestData {
  val propertyAddress = Address("1 The Road", Some("The Town"), None, "AA11 1AA")
  val fromPostcodeLookup = LookupServiceAddress("1 The Road", Some("The Town"), None, "AA11 1AA", "GB1234567890")
  val unchanged = Address("1 The Road", Some("The Town"), None, "AA11 1AA")
  val manual = Address("1 The Road", Some("The Town"), None, "AA11 1AA")
  val oneLineChanged = Address("1A The Road", Some("The Town"), None, "AA11 1AA")
  val twoLinesChanged = Address("1 The Other Road", Some("The Other Town"), None, "AA11 1AA")
  val overseas = Address("1 The Road", Some("Atlantis"), None, "The Sea")
}

object TestAddressAuditing extends AddressAuditing {
  protected val audit = StubAuditer
}

object StubAuditer extends Audit with Matchers {
  private case class AuditEvent(event: String, detail: Map[String, String])
  private var lastSentAudit: AuditEvent = null

  override def apply(event: String, detail: Map[String, String])(implicit hc: HeaderCarrier) = {
    lastSentAudit = AuditEvent(event, detail)
    Future.successful(Disabled)
  }

  def mustHaveSentAudit(event: String, detail: Map[String, String]) = {
    lastSentAudit should not equal(null)
    lastSentAudit.event should equal (event)
    detail foreach { d =>
      lastSentAudit.detail should contain (d)
    }
    lastSentAudit = null
  }
}
