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

package filters

import config.SessionTimeoutFilter
import org.joda.time.DateTime
import org.scalatest.OptionValues._
import play.api.Play
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.test.{FakeApplication, FakeHeaders, FakeRequest}
import play.filters.csrf.CSRFConf
import useCases.Now
import utils.UnitTest

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, _}

class SessionTimeoutFilterSpec extends UnitTest {
  val now_ = new DateTime(2016, 1, 15, 14, 23)
  val timeout = 24 hours
  val returnOk: RequestHeader => Future[Result] = h => Future.successful(Ok("").withSession(h.session))
  val lastRequestTsKey = "lastrequesttimestamp"

  "Session timeout filter" when {
    Play.start(FakeApplication())
    val f = new SessionTimeoutFilter {
      override def now: Now = () => now_
      override val timeoutDuration: Duration = timeout
    }

    "the session has no last request timestamp" should {
      val req = FakeRequest(
        "GET", controllers.dataCapturePages.routes.PageController.showPage(3).url,
        FakeHeaders(), ""
      ).withSession()

      "set the current time as the time of the last request in the sesion" in {
        val res = await(f(returnOk)(req))
        val ts = res.session(null).get(lastRequestTsKey)
        assert(ts.value === now_.toString)
      }
    }

    "the session already has a last request timestamp" should {
      val req = FakeRequest(
        "GET", controllers.dataCapturePages.routes.PageController.showPage(3).url,
        FakeHeaders(), ""
      ).withSession(lastRequestTsKey -> now_.minusSeconds(30).toString)

      "overwrite the last request timestamp with the current time" in {
        val res = await(f(returnOk)(req))
        val ts = res.session(null).get(lastRequestTsKey)
        assert(ts.value === now_.minusSeconds(30).toString)
      }
    }

    "the time since the last request timestamp exceeds the timeout window" should {
      val req = FakeRequest(
        "GET", controllers.dataCapturePages.routes.PageController.showPage(3).url,
        FakeHeaders(), ""
      ).withSession(lastRequestTsKey -> now_.minusMinutes(timeout.toMinutes.toInt + 1).toString)

      val res = await(f(returnOk)(req))

      "redirect to the session timeout page" in {
        assert(res.header.status === 303)
        val loginUrl = controllers.routes.Application.sessionTimeout().url
        assert(res.header.headers.get("location").value === loginUrl)
      }

      "clear the session" in {
        assert(res.session(FakeRequest()).isEmpty)
      }
    }
  }
}
