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

import config.{ForGlobal, SessionTimeoutFilter}
import org.joda.time.DateTime
import org.scalatest.OptionValues._
import play.api.Play
import play.api.mvc.Results._
import play.api.mvc.{EssentialFilter, RequestHeader, Result}
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeHeaders, FakeRequest}
import uk.gov.hmrc.play.frontend.filters.SessionCookieCryptoFilter
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

      "set the current time as the time of the last request in the session" in {
        val res = await(f(returnOk)(req))
        val ts = res.session(null).get(lastRequestTsKey)
        assert(ts.value === now_.toString)
      }
    }

    "the session already has a last request timestamp" should {
      val req = FakeRequest("GET", controllers.dataCapturePages.routes.PageController.showPage(3).url)
                  .withSession(lastRequestTsKey -> now_.minusSeconds(30).toString)
      val res = await(f(returnOk)(req))

      "overwrite the last request timestamp with the current time" in {
        val ts = res.session(null).get(lastRequestTsKey)
        assert(ts.value === now_.toString)
      }
    }

    "the time since the last request timestamp exceeds the timeout window" should {
      val req = FakeRequest("GET", controllers.dataCapturePages.routes.PageController.showPage(3).url)
                  .withSession(lastRequestTsKey -> now_.minusMinutes(timeout.toMinutes.toInt + 1).toString)
      val res = await(f(returnOk)(req))

      "redirect to the session timeout page" in {
        assert(res.header.status === 303)
        val loginUrl = controllers.routes.Application.sessionTimeout().url
        assert(res.header.headers.get("location").value === loginUrl)
      }

      "clear the session and add the request timestamp" in {
        assert(res.session(FakeRequest()).data === Map(lastRequestTsKey -> now_.toString))
      }
    }
  }

  "Session timeout filter" when {
    val g = new ForGlobal {
      override def frontendFilters: Seq[EssentialFilter] = super.frontendFilters.filterNot(_.getClass == SessionCookieCryptoFilter.getClass)
    }
    Play.start(FakeApplication(withGlobal = Some(g)))

    "trying to switch to Welsh languages after a session has timed out" should {
      val req = FakeRequest("GET", controllers.routes.CustomLanguageController.showWelsh().url)
                  .withSession(lastRequestTsKey -> now_.minusMinutes(timeout.toMinutes.toInt + 1).toString)
      val Some(res) = route(req)

      "still continue to change the language" in {
        assert(status(res) === 303)
        assert(header("location", res).value === controllers.routes.CustomLanguageController.switchToLanguage("cymraeg").url)
      }
    }

    "trying to switch to English languages after a session has timed out" should {
      val req = FakeRequest("GET", controllers.routes.CustomLanguageController.showEnglish().url)
                  .withSession(lastRequestTsKey -> now_.minusMinutes(timeout.toMinutes.toInt + 1).toString)
      val Some(res) = route(req)

      "still continue to change the language" in {
        assert(status(res) === 303)
        assert(header("location", res).value === controllers.routes.CustomLanguageController.switchToLanguage("english").url)
      }
    }
  }
}
