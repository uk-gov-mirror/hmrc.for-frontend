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

import akka.stream.Materializer
import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import models.serviceContracts.submissions.Submission
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.OWrites
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.config.AuditingConfig
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.play.audit.model.{DataEvent, ExtendedDataEvent}
import uk.gov.hmrc.play.audit.AuditExtensions._

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ForAuditConnector])
trait Audit extends AuditConnector {

  implicit def ec: ExecutionContext

  def apply(event: String, detail: Map[String, String])(implicit hc: HeaderCarrier): Future[AuditResult] = {
    val tags = hc.toAuditTags()
    val de = DataEvent(auditSource = "for-frontend", auditType = event, tags = tags, detail = detail)
    sendEvent(de)
  }

  def apply(event: String, submission: Submission)(implicit hc: HeaderCarrier): Future[AuditResult] = {
    val sub = (implicitly[OWrites[Submission]]).writes(submission)
    val de = ExtendedDataEvent(auditSource = "for-frontend", auditType = event, detail = sub)
    sendExtendedEvent(de)
  }

}

object Audit {
  val referenceNumber = "referenceNumber"
}

@Singleton
class ForAuditConnector @Inject() (val auditingConfig: AuditingConfig,
                                   override val materializer: Materializer,
                                   override val lifecycle: ApplicationLifecycle
                                  )(implicit val ec: ExecutionContext) extends Audit {
}
