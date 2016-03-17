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

package connectors

import models.serviceContracts.submissions.Submission
import play.api.libs.json.Json
import playconfig.WSHttp
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._

import scala.concurrent.Future

object SubmissionConnector extends SubmissionConnector with ServicesConfig {
  lazy val serviceUrl = baseUrl("for-hod-adapter")

  def submit(refNum: String, submission: Submission)(implicit hc: HeaderCarrier): Future[Unit] = {
    WSHttp.PUT(s"$serviceUrl/for/submissions/$refNum", submission).map(_ => ())
  }
}

trait SubmissionConnector {
  def submit(refNum: String, submisson: Submission)(implicit hc: HeaderCarrier): Future[Unit]
}
