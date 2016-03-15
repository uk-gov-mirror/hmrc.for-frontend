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

import models._
import models.serviceContracts.submissions._
import scala.Left
import scala.Right
import play.api.data.FormError
import play.api.data.format.Formatter

object Formats {
  
  def namedEnumFormatter[T <: NamedEnum](named:NamedEnumSupport[T],missingCode:String): Formatter[T] = new Formatter[T] {

    override val format = Some((missingCode, Nil))

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
      val resOpt = for {
        keyVal <- data.get(key)
        enumTypeValue <- named.fromName(keyVal)
      } yield {
          Right(enumTypeValue)
        }
      resOpt.getOrElse(Left(Seq(FormError(key, missingCode, Nil))))
    }

    def unbind(key: String, value: T) = Map(key -> value.name)
  }

  implicit val userTypeFormat: Formatter[UserType] = namedEnumFormatter(UserTypes, Errors.noValueSelected)
  implicit val contactTypeFormat: Formatter[ContactType] = namedEnumFormatter(ContactTypes, Errors.noValueSelected)
  implicit val contactAddressTypeFormat: Formatter[ContactAddressType] = namedEnumFormatter(ContactAddressTypes, Errors.required)
  implicit val propertyTypeFormat: Formatter[PropertyType] = namedEnumFormatter(PropertyTypes, Errors.noValueSelected)
  implicit val occupierTypeFormat: Formatter[OccupierType] = namedEnumFormatter(OccupierTypes, Errors.noValueSelected)
  implicit val landlordConnTypeFormat: Formatter[LandlordConnectionType] = namedEnumFormatter(LandlordConnectionTypes, Errors.noValueSelected)
  implicit val leaseAgreementTypeFormat: Formatter[LeaseAgreementType] = namedEnumFormatter(LeaseAgreementTypes, Errors.noValueSelected)
  implicit val reviewIntervalTypeFormat: Formatter[ReviewIntervalType] = namedEnumFormatter(ReviewIntervalTypes, Errors.noValueSelected)
  implicit val rentBaseTypeFormat: Formatter[RentBaseType] = namedEnumFormatter(RentBaseTypes, Errors.noValueSelected)
  implicit val rentFixedByTypeFormat: Formatter[RentFixedType] = namedEnumFormatter(RentFixedTypes, Errors.noValueSelected)
  implicit val notReviewRentFixedTypeFormat: Formatter[NotReviewRentFixedType] = namedEnumFormatter(NotReviewRentFixedTypes, Errors.noValueSelected)
  implicit val rentSetByTypesFormat: Formatter[RentSetByType] = namedEnumFormatter(RentSetByTypes, Errors.noValueSelected)
  implicit val responsibleTypesFormat: Formatter[ResponsibleType] = namedEnumFormatter(ResponsibleTypes, Errors.noValueSelected)
  implicit val formatRentLengthType: Formatter[RentLengthType] = namedEnumFormatter(RentLengthTypes, Errors.noValueSelected)
  //implicit val tenantsAddressType: Formatter[TenantsAddressType] = namedEnumFormatter(TenantsAddressTypes, Errors.noValueSelected)
  implicit val satisfactionFormatter: Formatter[Satisfaction] = namedEnumFormatter(SatisfactionTypes, Errors.noValueSelected)
}
