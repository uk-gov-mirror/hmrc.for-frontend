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

import actions.{RefNumAction, RefNumRequest}
import form.MappingSupport
import form.persistence.FormDocumentRepository
import javax.inject.{Inject, Singleton}
import models.pages.SummaryBuilder
import org.apache.commons.lang3.StringUtils
import play.api.data.Forms._
import play.api.data.{Form, _}
import play.api.data.format.Formatter
import play.api.data.validation.Constraints._
import play.api.data.validation.{Constraint, Valid}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.{Configuration, Logger}
import playconfig.{FormPersistence, SessionId}
import uk.gov.hmrc.play.frontend.controller.FrontendController


@Singleton
class NotConnectedController @Inject()(configuration: Configuration)(implicit val messagesApi: MessagesApi) extends FrontendController with I18nSupport {

  val logger = Logger(classOf[NotConnectedController])

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

  def repository: FormDocumentRepository = FormPersistence.formDocumentRepository

  def findSummary(implicit request: RefNumRequest[_]) = {
    repository.findById(SessionId(hc), request.refNum) flatMap {
      case Some(doc) => Option(SummaryBuilder.build(doc))
      case None => None
    }
  }

  def onPageView = RefNumAction.async { implicit request =>
    findSummary.map {
      case Some(summary) => Ok(views.html.notConnected(form, summary))
      case None => {
        logger.error(s"Could not find document in current session - ${request.refNum} - ${hc.sessionId}")
        InternalServerError(views.html.error.error500())
      }
    }
  }

  def onPageSubmit = RefNumAction.async { implicit request =>

    findSummary.map {
      case Some(summary) => {
        form.bindFromRequest().fold({ formWithErrors =>
          Ok(views.html.notConnected(formWithErrors, summary))
        }, { formWithData =>
          Ok("stored in db")
        })
      }
      case None => {
        logger.error(s"Could not find document in current session - ${request.refNum} - ${hc.sessionId}")
        InternalServerError(views.html.error.error500())
      }
    }
  }

}

case class NotConnectedPropertyForm(
                                 fullName: String,
                                 email: Option[String],
                                 phoneNumber: Option[String],
                                 additionalInformation: String
                               )
