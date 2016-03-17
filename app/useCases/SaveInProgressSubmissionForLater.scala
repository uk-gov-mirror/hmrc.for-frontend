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

package useCases

import connectors.{Document, HODConnector}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import playconfig.{FormPersistence, SessionId}
import security.LoginToHOD.AuthToken
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.logging.Authorization

import scala.concurrent.Future
import scala.util.Random

object SaveInProgressSubmissionForLater {
  type SaveInProgressSubmissionForLater = HeaderCarrier => (Document, HeaderCarrier) => Future[SaveForLaterPassword]
  type UpdateDocumentInCurrentSession = (HeaderCarrier, ReferenceNumber, Document) => Future[Unit]
  type GenerateSaveForLaterPassword = () => String
  type StoreInProgressSubmission = Document => Future[Unit]

  def apply(gp: GenerateSaveForLaterPassword, s: StoreInProgressSubmission, u: UpdateDocumentInCurrentSession)
           (d: Document, hc: HeaderCarrier): Future[String] = {
    val p = d.saveForLaterPassword getOrElse gp()
    val nd = d.copy(saveForLaterPassword = Some(p))
    s(nd) map { _ =>  u(hc, d.referenceNumber, nd) } map { _ => p }
  }
}

object StoreInProgressSubmissionFor90Days {
  def apply(d: Document)(implicit hc: HeaderCarrier): Future[Unit] = HODConnector.saveForLater(d)
}

object LoadSavedForLaterDocument {
  def apply(a: AuthToken, r: ReferenceNumber)(implicit hc: HeaderCarrier): Future[Option[Document]] =
    HODConnector.loadSavedDocument(r)(hc.copy(authorization = Some(Authorization(a))))
}

object UpdateDocumentInCurrentSession {
  def apply(h: HeaderCarrier, r: ReferenceNumber, d: Document): Future[Unit] =
    FormPersistence.formDocumentRepository.store(SessionId(h), r, d)
}

object Generate7LengthLowercaseAlphaNumPassword {
  val validDigits = (2 to 9).toSeq
  val validChars = "abcdefghjkmnpqrstuvwxyz".toCharArray
  val allValid: Seq[Any] = validDigits ++ validChars

  def apply(): SaveForLaterPassword = (1 to 7).map { _ => Random.shuffle(allValid).head }.mkString
}
