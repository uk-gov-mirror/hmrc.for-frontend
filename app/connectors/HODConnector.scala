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

package connectors

import config.ForConfig
import controllers.toFut
import helpers.RunModeHelper
import models.FORLoginResponse
import models.serviceContracts.submissions.{AddressConnectionTypeYes, AddressConnectionTypeYesChangeAddress}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{Format, JsValue}
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http._
import useCases.ReferenceNumber
import util.Constant
import views.html.helper.urlEncode

import scala.concurrent.Future
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, HttpReads, HttpResponse, NotFoundException, Upstream4xxResponse}

object HODConnector extends HODConnector with ServicesConfig with RunModeHelper {
  implicit val f: Format[Document] = Document.formats


  lazy val serviceUrl = baseUrl("for-hod-adapter")
  lazy val emailUrl = baseUrl("email")

  val http = ForConfig.http

  private def url(path: String) = s"$serviceUrl/for/$path"

  def readsHack(implicit httpReads: HttpReads[FORLoginResponse]) = {
    new HttpReads[FORLoginResponse] {
      override def read(method: String, url: String, response: HttpResponse): FORLoginResponse = {
        response.status match {
          case 400 => throw new BadRequestException(response.body)
          case 401 => throw new Upstream4xxResponse(response.body, 401, 401, response.allHeaders)
          case _ => httpReads.read(method, url, response)
        }
      }
    }
  }

  override def verifyCredentials(ref1: String, ref2: String, postcode: String)(implicit hc: HeaderCarrier): Future[FORLoginResponse] = {
    val parts = Seq(ref1, ref2, postcode).map(urlEncode)
    http.GET[FORLoginResponse](url(s"${parts.mkString("/")}/verify"))(readsHack, hc, defaultContext)
  }

  def saveForLater(d: Document)(implicit hc: HeaderCarrier): Future[Unit] =
    http.PUT(url(s"savedforlater/${d.referenceNumber}"), d) map { _ => () }

  def loadSavedDocument(r: ReferenceNumber)(implicit hc: HeaderCarrier): Future[Option[Document]] = {
    http.GET[Document](url(s"savedforlater/$r")).map(Some.apply).map(splitAddress) recoverWith {
      case n: NotFoundException => None
    }
  }


  def splitAddress(maybeDocument: Option[Document]): Option[Document] = {
    val fixedDocument = for {
      doc <- maybeDocument
      page1 <- doc.page(1)
      isAddressCorrect <- page1.fields.get("isAddressCorrect")
    } yield {
      if(isAddressCorrect.contains("false")) {
        updateChangedAddresToNewModel(doc, page1)
      }else {
        val page0 = Page(0, form.PageZeroForm.pageZeroForm.fill(AddressConnectionTypeYes).data.mapValues(Seq(_)) )
        updateDocWithPageZeroAndRemovePageOne(doc, page0)
      }
    }
    fixedDocument.orElse(maybeDocument)
  }

  def updateChangedAddresToNewModel(document: Document, page1: Page): Document = {
    val page1Data = page1.fields.map { case (key, value) =>
      if(key.startsWith("address.")) {
        (key.replace("address.", ""), value)
      }else {
        (key, value)
      }
    }.filterKeys(_ != "isAddressCorrect")

    val newPage1 = page1.copy(fields = page1Data)

    val page0 = Page(0, form.PageZeroForm.pageZeroForm.fill(AddressConnectionTypeYesChangeAddress).data.mapValues(Seq(_)))

    val newPages = Seq(page0, newPage1) ++ (document.pages.filterNot(x => x.pageNumber == 0 || x.pageNumber == 1))

    document.copy(pages = newPages)

  }



  def updateDocWithPageZeroAndRemovePageOne(document: Document, page0:Page) = {
    val newPages = page0 +: (document.pages.filterNot(x => x.pageNumber == 0 || x.pageNumber == 1))
    document.copy(pages = newPages)
  }



  def getSchema(schemaName: String)(implicit hc: HeaderCarrier): Future[JsValue] = {
    http.GET[JsValue](url(s"schema/$schemaName"))
  }
}

trait HODConnector {
  def verifyCredentials(ref1: String, ref2: String, postcode: String)(implicit hc: HeaderCarrier): Future[FORLoginResponse]
}
