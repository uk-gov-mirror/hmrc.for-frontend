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

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.google.inject.ImplementedBy
import config.ForConfig
import javax.inject.{Inject, Singleton}
import models.serviceContracts.submissions.{NotConnectedSubmission, Submission}
import play.api.Mode.Mode
import play.api.{Application, Configuration, Play}
import play.api.http.HttpEntity
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsValue
import play.api.mvc.{ResponseHeader, Result}
import uk.gov.hmrc.play.config.ServicesConfig

import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

@Singleton
class HodSubmissionConnector @Inject() (application: Application) extends SubmissionConnector with ServicesConfig {
  lazy val serviceUrl = baseUrl("for-hod-adapter")
  val http = ForConfig.http

  def submit(refNum: String, submission: Submission)(implicit hc: HeaderCarrier): Future[Unit] = {
    http.PUT(s"$serviceUrl/for/submissions/$refNum", submission).map(_ => ())
  }

  def submit(refNum: String, submission: JsValue)(implicit hc: HeaderCarrier): Future[Result] = {
    http.PUT(s"$serviceUrl/for/submissions/$refNum", submission) map { r =>
      Result(ResponseHeader(r.status), HttpEntity.Streamed(Source.single(ByteString(Option(r.body).getOrElse(""))), None, None))
    }
  }

  override def submitNotConnected(refNumber: String, submission: NotConnectedSubmission)(implicit hc: HeaderCarrier): Future[Unit] = {
    http.PUT(s"$serviceUrl/for/submissions/notConnected/${submission._id}", submission).map(_ => ())
  }

  override protected def mode: Mode = application.mode

  override protected def runModeConfiguration: Configuration = application.configuration

}

object SubmissionConnector {
  def apply():SubmissionConnector = {
    Play.current.injector.instanceOf(classOf[SubmissionConnector])
  }
}

@ImplementedBy(classOf[HodSubmissionConnector])
trait SubmissionConnector {
  def submit(refNum: String, submisson: Submission)(implicit hc: HeaderCarrier): Future[Unit]
  def submitNotConnected(refNumber: String, submission: NotConnectedSubmission)(implicit hc: HeaderCarrier): Future[Unit]
}
