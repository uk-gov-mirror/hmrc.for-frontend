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

package models.serviceContracts.submissions

import form.{MappingSupport, NotConnectedPropertyForm}
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import play.api.data.{FieldMapping, Form, FormError, Mapping}
import play.api.data.Forms.mapping
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Valid}
import play.api.data.validation.Constraints.emailAddress
import play.api.libs.json._
import play.api.data.Forms._

case class NotConnectedSubmission( _id: String /** submission Id*/,
                                   address: String,
                                   fullName: String,
                                   emailAddress: Option[String],
                                   phoneNumber: Option[String],
                                   additionalInformation: Option[String],
                                   createdAt: DateTime
                                 )

object NotConnectedSubmission {
  implicit  val format = Json.format[NotConnectedSubmission]

  def atLeastOneKeyFormatter(anotherKey: String):Formatter[Option[String]] = new Formatter[Option[String]] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] = {
      if(data.get(key).exists(value => StringUtils.isBlank(value))) {
        if(data.get(anotherKey).exists(value => StringUtils.isBlank(value))) {
          Left(Seq(FormError(key, "notConnected.emailOrPhone")))
        }else {
          Right(None)
        }
      }else {
        Right(Some(data(key).trim))
      }
    }

    override def unbind(key: String, value: Option[String]): Map[String, String] = value.map(x => Map(key -> x))
      .getOrElse(Map.empty[String, String])
  }

  def atLeastOneMapping(anotherKey: String, constraints: Constraint[String]*): Mapping[Option[String]] = {
    def optConstraint[T](constraint: Constraint[T]): Constraint[Option[T]] = Constraint[Option[T]] { (parameter: Option[T]) =>
      parameter match {
        case Some(value) => constraint(value)
        case None => Valid
      }
    }
    FieldMapping(key = "", constraints.map(optConstraint(_)))(atLeastOneKeyFormatter(anotherKey))
  }

  val form = Form(
    mapping(
      "fullName" -> nonEmptyText,
      "email" -> atLeastOneMapping("phoneNumber", emailAddress),
      "phoneNumber" -> atLeastOneMapping("email", MappingSupport.phoneNumber.constraints:_*),
      "additionalInformation" -> text
    )(NotConnectedPropertyForm.apply)(NotConnectedPropertyForm.unapply)
  )


}
