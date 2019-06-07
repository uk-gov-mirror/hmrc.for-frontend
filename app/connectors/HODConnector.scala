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

import config.ForConfig
import controllers.toFut
import helpers.RunModeHelper
import models.FORLoginResponse
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{Format, JsValue}
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http._
import useCases.ReferenceNumber
import views.html.helper.urlEncode

import scala.concurrent.Future
import uk.gov.hmrc.http.{HeaderCarrier, NotFoundException}

object HODConnector extends HODConnector with ServicesConfig with RunModeHelper {
  implicit val f: Format[Document] = Document.formats

  val PAGENO = 2
  lazy val serviceUrl = baseUrl("for-hod-adapter")
  lazy val emailUrl = baseUrl("email")

  val http = ForConfig.http

  private def url(path: String) = s"$serviceUrl/for/$path"

  override def verifyCredentials(ref1: String, ref2: String, postcode: String)(implicit hc: HeaderCarrier): Future[FORLoginResponse] = {
    val parts = Seq(ref1, ref2, postcode).map(urlEncode)
    http.GET[FORLoginResponse](url(s"${parts.mkString("/")}/verify"))
  }

  def saveForLater(d: Document)(implicit hc: HeaderCarrier): Future[Unit] =
    http.PUT(url(s"savedforlater/${d.referenceNumber}"), d) map { _ => () }

  def loadSavedDocument(r: ReferenceNumber)(implicit hc: HeaderCarrier): Future[Option[Document]] = {
    http.GET[Document](url(s"savedforlater/$r")).map(Some.apply).map(removeOwnerAndOccupiers).map(removeRentLengthType) recoverWith {
      case n: NotFoundException => None
    }
  }

  def removeRentLengthType(maybeDocument: Option[Document]): Option[Document] = {

    val removedPage9 = for {
      doc <- maybeDocument
      page9 <- doc.page(9)
      _ <- page9.fields.get("totalRent.rentLengthType")
    } yield {
      val newPage9 = page9.copy(fields = page9.fields - ("totalRent.annualRentExcludingVat", "totalRent.rentLengthType" ))
      doc.copy(pages = doc.pages.filterNot(_.pageNumber == 9))
    }

    if(removedPage9.isDefined) {
      removedPage9
    }else {
      maybeDocument
    }

  }

  def removeOwnerAndOccupiers (savedDocument: Option[Document]) :Option[Document] = {
    val changedPage2 = for {
      document <- savedDocument
      page2 <- document.page(PAGENO)

    } yield  {
        val userType = page2.fields("userType")(0)
        userType match {
          case "ownerOccupier" =>  {
            val updatedfields = (page2.fields - "userType") + ("userType" -> Seq("owner") )
            val page_2 = page2.copy(fields = updatedfields)
            val allPages = ((document.pages.filterNot(_.pageNumber == 2)) :+ page_2).sortBy(_.pageNumber)
            document.copy(pages = allPages)
          }
          case _ => document
        }
      }
      changedPage2  match {
        case Some(x) => changedPage2
        case _ => savedDocument
      }
  }


  def getSchema(schemaName: String)(implicit hc: HeaderCarrier): Future[JsValue] = {
    http.GET[JsValue](url(s"schema/$schemaName"))
  }
}

trait HODConnector {
  def verifyCredentials(ref1: String, ref2: String, postcode: String)(implicit hc: HeaderCarrier): Future[FORLoginResponse]
}
