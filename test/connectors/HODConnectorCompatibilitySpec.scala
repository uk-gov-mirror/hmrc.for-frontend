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

package connectors

import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}
import org.scalatestplus.play.OneServerPerSuite


/**
  * Test compatibility after rentLengthType removal.
  *
  * This test was created to test code which remove page9 from saved documents.
  * We removed one question from page 9 and this question changes meaning of
  * anualRent field. To prevent submission of wrong data, we removing whole page9.
  * Probably after 6 months this code can be removed.
  */
class HODConnectorCompatibilitySpec extends FlatSpec with Matchers with OneServerPerSuite{

  val oldPage1WithoutAddress = Page(1, Map(
    "isAddressCorrect" -> Seq("true")
  ))

  val newPage0WithoutAddress = Page(0, Map(
    "isRelated" -> Seq("yes")
  ))

  val newPage0WithAddress = Page(0, Map(
    "isRelated" -> Seq("yes-change-address")
  ))

  val oldPage1WithAddress = Page(1, Map(
    "isAddressCorrect" -> Seq("false"),
    "address.buildingNameNumber" -> Seq("10"),
    "address.street1" -> Seq("10"),
    "address.street2" -> Seq("10")
  ))

  val newPage1WithAddress = Page(1, Map(
    "buildingNameNumber" -> Seq("10"),
    "street1" -> Seq("10"),
    "street2" -> Seq("10")
  ))


  val journeyStarted = DateTime.now()


  val oldDocumentWithoutAddress = Some(Document(
    "referenceNumber", journeyStarted, Seq(oldPage1WithoutAddress), None, None, Seq.empty
  ))

  val newDocumentWithoutAddress = Some(Document(
    "referenceNumber", journeyStarted, Seq(newPage0WithoutAddress), None, None, Seq.empty
  ))

  val oldDOcumentWithAddress = Some(Document(
    "referenceNumber", journeyStarted, Seq(oldPage1WithAddress), None, None, Seq.empty
  ))

  val newDocumentWithAddress = Some(Document(
    "referenceNumber", journeyStarted, Seq(newPage0WithAddress, newPage1WithAddress), None, None, Seq.empty
  ))


  "HODConnector" should "populate page0 for saved submission without address change" in {
    val connector = HODConnector
    val testDocument = connector.splitAddress(oldDocumentWithoutAddress)

    testDocument shouldBe newDocumentWithoutAddress

  }

  it should "populate page0 and page1 for saved submission with changed address" in {
    val connector = HODConnector
    val testDocument = connector.splitAddress(oldDOcumentWithAddress)

    testDocument shouldBe newDocumentWithAddress
  }

}
