/*
 * Copyright 2017 HM Revenue & Customs
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

package form

import org.scalatest.{ FlatSpec, Matchers }
import utils.FormBindingTestAssertions._
import org.joda.time.DateTime

class LoginMappingSpec extends FlatSpec with Matchers {
	val loginForm = controllers.LoginController.loginForm

	behavior of "Login Mapping"

	it should "bind to ISO date time strings for the start-time" in {
		val data = Map(
			"ref1" -> "1111111",
			"ref2" -> "323",
			"postcode" -> "AA11 1AA",
			"start-time" -> "2016-01-04T08:58:42.113Z"
		)

		mustBind(loginForm.bind(data)) { x =>
			assert(x.startTime === new DateTime(2016, 1, 4, 8, 58, 42, 113))
		}
	}

	it should "only allow 7 or 8 digit numeric reference numbers" in {
		val data = Map(
			"ref2" -> "323",
			"postcode" -> "AA11 1AA",
			"start-time" -> "2016-01-04T08:58:42.113Z"
		)

		val d6 = data.updated("ref1", "666666")
		mustContainError("ref1", Errors.invalidRefNum, loginForm.bind(d6))

    val d7 = data.updated("ref1", "7777777")
    mustBind(loginForm.bind(d7)) { x => assert(x.ref1 === "7777777") }

    val d8 = data.updated("ref1", "88888888")
    mustBind(loginForm.bind(d8)) { x => assert(x.ref1 === "88888888") }

    val d9 = data.updated("ref1", "999999999")
    mustContainError("ref1", Errors.invalidRefNum, loginForm.bind(d9))
	}

}
