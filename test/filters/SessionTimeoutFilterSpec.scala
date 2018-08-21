/*
 * Copyright 2018 HM Revenue & Customs
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

import config.{ForGlobal, SessionTimeoutFilter}
import org.joda.time.DateTime
import org.scalatest.OptionValues._
import org.scalatestplus.play.guice.{GuiceFakeApplicationFactory, GuiceOneAppPerTest, GuiceOneServerPerTest}
import play.api.Logger
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Results._
import play.api.mvc.{EssentialFilter, RequestHeader, Result}
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest}
import uk.gov.hmrc.play.frontend.filters.SessionCookieCryptoFilter
import useCases.Now
import utils.UnitTest

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, _}

class SessionTimeoutFilterSpec extends UnitTest with GuiceOneAppPerTest with GuiceFakeApplicationFactory {

  override def fakeApplication() = {
    new GuiceApplicationBuilder().configure(Map("auditing.enabled" -> "false")).build()
  }

  val now_ = new DateTime(2016, 1, 15, 14, 23)
  val timeout = 24 hours
  val returnOk: RequestHeader => Future[Result] = h => Future.successful(Ok("").withSession(h.session))
  val lastRequestTsKey = "lastrequesttimestamp"

  "Session timeout filter" when {
    val f = new SessionTimeoutFilter {
      override def now: Now = () => now_

      override val timeoutDuration: Duration = timeout
    }

    "the session has no last request timestamp" should {
      "set the current time as the time of the last request in the session" in {
          val req = FakeRequest(
            "GET", controllers.dataCapturePages.routes.PageController.showPage(3).url,
            FakeHeaders(), ""
          ).withSession()

          val res = await(f(returnOk)(req))
          val ts = res.session(null).get(lastRequestTsKey)
          assert(ts.value === now_.toString)
        }
    }

    "the session already has a last request timestamp" should {
      "overwrite the last request timestamp with the current time" in {
          val req = FakeRequest("GET", controllers.dataCapturePages.routes.PageController.showPage(3).url)
            .withSession(lastRequestTsKey -> now_.minusSeconds(30).toString)
          val res = await(f(returnOk)(req))

          val ts = res.session(null).get(lastRequestTsKey)
          assert(ts.value === now_.toString)
        }
    }

    "the time since the last request timestamp exceeds the timeout window" should {
      "redirect to the session timeout page" in {
          val req = FakeRequest("GET", controllers.dataCapturePages.routes.PageController.showPage(3).url)
            .withSession(lastRequestTsKey -> now_.minusMinutes(timeout.toMinutes.toInt + 1).toString)
          val res = await(f(returnOk)(req))
          assert(res.header.status === 303)
          val loginUrl = controllers.routes.Application.sessionTimeout().url
          assert(res.header.headers.get("location").value === loginUrl)
      }

      "clear the session and add the request timestamp" in {
          val req = FakeRequest("GET", controllers.dataCapturePages.routes.PageController.showPage(3).url)
            .withSession(lastRequestTsKey -> now_.minusMinutes(timeout.toMinutes.toInt + 1).toString)
          val res = await(f(returnOk)(req))
          assert(res.session(FakeRequest()).data === Map(lastRequestTsKey -> now_.toString))
      }
    }
  }

  "Session timeout filter" when {
    "trying to switch to Welsh languages after a session has timed out" should {
      "still continue to change the language" in {
        val req = FakeRequest("GET", controllers.routes.CustomLanguageController.showWelsh().url)
          .withSession(lastRequestTsKey -> now_.minusMinutes(timeout.toMinutes.toInt + 1).toString)
        val Some(res) = route(req)
        assert(status(res) === 303)
        assert(header("location", res).value === controllers.routes.LoginController.show().url)
        assert(cookies(res).get("PLAY_LANG").value.value === "cy")
      }
    }

    "trying to switch to English languages after a session has timed out" should {
      "still continue to change the language" in {
        val req = FakeRequest("GET", controllers.routes.CustomLanguageController.showEnglish().url)
          .withSession(lastRequestTsKey -> now_.minusMinutes(timeout.toMinutes.toInt + 1).toString)
        val Some(res) = route(req)

        assert(status(res) === 303)
        assert(header("location", res).value === controllers.routes.LoginController.show().url)
        assert(cookies(res).get("PLAY_LANG").value.value === "en")
      }
    }
  }
}
