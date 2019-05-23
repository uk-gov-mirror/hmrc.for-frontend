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

package controllers

import form.MappingSupport
import javax.inject.{Inject, Singleton}
import models.pages.Summary
import models.serviceContracts.submissions.Address
import org.joda.time.DateTime
import play.api.Configuration
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, Controller}
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.data.validation.Constraints._
import uk.gov.voa.play.form.ConditionalMappings
import uk.gov.voa.play.form.ConditionalMappings._
import play.api.data.format.Formats._
import play.api.data.format.Formatter


@Singleton
class NotConnectedController @Inject()(configuration: Configuration)(implicit val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def stringOptionFormatter(anotherKey: String):Formatter[Option[String]] = new Formatter[Option[String]] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] = {
      if("" == data.get(key).getOrElse("")) {
        if("" == data.get(anotherKey).getOrElse("")) {
          Left(Seq(FormError(key, s"key.for.message.bundle${anotherKey}")))
        }else {
          Right(None)
        }
      }else {
        Right(Some(data(key)))
      }
    }

    override def unbind(key: String, value: Option[String]): Map[String, String] = value.map(x => Map(key -> x))
      .getOrElse(Map.empty[String, String])
  }

  def atLeastOneMapping(anotherKey: String, constraints: Constraint[String]*): Mapping[Option[String]] = {
    def optConstraint[T](constraint: Constraint[T]): Constraint[Option[T]] = Constraint[Option[T]] { (parameter: Option[T]) =>
      parameter match {
        case a: Some[T] => constraint.apply(a.get)
        case None => Valid
      }
    }

    FieldMapping(key = "", constraints.map(optConstraint(_)))(stringOptionFormatter(anotherKey))
    of[Option[String]](stringOptionFormatter(anotherKey))
  }


  val form = Form(
    mapping(
      "fullName" -> text,
      "email" -> atLeastOneMapping("phoneNumber", emailAddress),
      "phoneNumber" -> atLeastOneMapping("email", MappingSupport.phoneNumber.constraints:_*),
      "additionalInformation" -> text
    )(NotConnectedProperty.apply)(NotConnectedProperty.unapply)
  )

  val summary = Summary (
      referenceNumber = "10643313719",
      journeyStarted = DateTime.now(),
      propertyAddress = None,
      customerDetails = None,
      theProperty = None,
      sublet = None,
      landlord = None,
      lease = None,
      rentReviews = None,
      rentAgreement = None,
      rent = None,
      rentIncludes = None,
      incentives = None,
      responsibilities = None,
      alterations = None,
      otherFactors = None,
      address = Option(Address("Unit 7", Option("Maltings Industrial Estate"), Option("Charmley"), "CH1 1AA"))
      )

  def onPageView = Action { implicit request =>


    Ok(views.html.notConnected(form, summary))
  }

  def onPageSubmit = Action { implicit request =>

    form.bindFromRequest().fold({ formWithErrors =>
      Ok(views.html.notConnected(formWithErrors, summary))
    }, { formWithData =>
      Ok("stored in db")
    })
  }

}

case class NotConnectedProperty(
                                 fullName: String,
                                 email: Option[String],
                                 phoneNumber: Option[String],
                                 additionalInformation: String
                               )
