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

import form.MappingSupport
import models.LookupServiceAddress
import models.pages.Summary
import models.serviceContracts.submissions.Address
import play.api.mvc.Request
import playconfig.Audit
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.Future

object AddressAuditing extends AddressAuditing {
  override protected val audit = Audit
}

trait AddressAuditing {
  protected val audit: Audit

  def apply(s: Summary, request: Request[_]): Future[Unit] = {
    s.propertyAddress.map(p1 => auditManualAddress(s.addressUserBelievesIsCorrect, request))
    s.sublet.foreach(p4 => p4.sublet.foreach(sub => auditManualAddress(sub.tenantAddress, request)))
    s.landlord.map(p5 => auditAddress(p5.originalLandlordAddress, p5.landlordAddress, request))
    Future.successful(())
  }

  private def auditAddress(original: Option[LookupServiceAddress], submitted: Option[Address], request: Request[_]): Future[Unit] = {
    (original, submitted) match {
      case (Some(o), Some(s)) if addressUnmodified(o, s) => auditAddressUnmodified(o, request)
      case (Some(o), Some(s)) if addressSignificantlyModified(o, s) => auditAddressSignificantlyModified(s, request)
      case (Some(o), Some(s)) if addressSlightlyModified(o, s) => auditAddressSlightlyModified(o, s, request)
      case (None, Some(s)) if internationalAddress(s) => auditInternationalAddress(s, request)
      case (None, Some(s)) => auditManualAddress(s, request)
      case _ => ()
    }
    Future.successful(())
  }

  private def addressUnmodified(o: LookupServiceAddress, s: Address) = {
    o.buildingNameNumber == s.buildingNameNumber &&
      o.street1 == s.street1 &&
      o.street2 == s.street2 &&
      o.postcode == s.postcode
  }

  private def addressSignificantlyModified(o: LookupServiceAddress, s: Address) = {
    val oLines = Seq(o.buildingNameNumber, o.street1, o.street2, o.postcode)
    val sLines = Seq(s.buildingNameNumber, s.street1, s.street2, s.postcode)
    oLines.zip(sLines).count(l => l._1 != l._2) >= 2
  }

  private def addressSlightlyModified(o: LookupServiceAddress, s: Address) = {
    val oLines = Seq(o.buildingNameNumber, o.street1, o.street2, o.postcode)
    val sLines = Seq(s.buildingNameNumber, s.street1, s.street2, s.postcode)
    oLines.zip(sLines).count(l => l._1 != l._2) == 1
  }

  private def internationalAddress(address: Address) = {
    !address.postcode.matches(MappingSupport.postcodeRegex)
  }

  private def auditAddressUnmodified(address: LookupServiceAddress, request: Request[_]) = {
    auditAddressChange("postcodeAddressSubmitted", address.toForAddress, request, additionalDetails = Map("submittedUPRN" -> address.uprn))
  }

  private def auditAddressSignificantlyModified(address: Address, request: Request[_]) = {
    auditAddressChange("manualAddressSubmitted", address, request)
  }

  private def auditAddressSlightlyModified(original: LookupServiceAddress, submitted: Address, request: Request[_]) = {
    auditAddressChange("postcodeAddressModifiedSubmitted", submitted, request,
      additionalDetails = Map(
        "originalLine1" -> original.buildingNameNumber,
        "originalLine2" -> original.street1.getOrElse(""),
        "originalLine3" -> original.street2.getOrElse(""),
        "originalPostcode" -> original.postcode,
        "originalUPRN" -> original.uprn
      )
    )
  }

  private def auditInternationalAddress(address: Address, request: Request[_]) = {
    auditAddressChange("internationalAddressSubmitted", address, request)
  }

  private def auditManualAddress(address: Address, request: Request[_]) = {
    auditAddressChange("manualAddressSubmitted", address, request)
  }

  private def auditAddressChange(auditType: String, address: Address, request: Request[_], additionalDetails: Map[String, String] = Map.empty) = {
    val hc = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))
    audit(
      auditType,
      detail = Map(
        "submittedLine1" -> address.buildingNameNumber,
        "submittedLine2" -> address.street1.getOrElse(""),
        "submittedLine3" -> address.street2.getOrElse(""),
        "submittedPostcode" -> address.postcode
      ) ++ additionalDetails,
      tags = Map(
        "X-Request-ID" -> hc.requestId.map(_.value).getOrElse(""),
        "X-Session-ID" -> hc.sessionId.map(_.value).getOrElse(""),
        "clientIP" -> hc.trueClientIp.getOrElse(""),
        "clientPort" -> hc.trueClientPort.getOrElse(""),
        "path" -> request.path,
        "transactionName" -> "sending_rental_information"
      )
    )(hc)
  }
}