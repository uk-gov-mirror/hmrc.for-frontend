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

import models.serviceContracts.submissions.Address
import org.scalatest.{ Matchers, FlatSpec }
import play.api.data.FormError
import play.api.data.Form
class FormSupportSpec extends FlatSpec with Matchers {


  "converting a field specific FormError" should "result in the same FormError" in {
    val fe1 = FormError("field", "message", "arg1")
    fe1.convert should be(fe1)
  }

  "converting a global FormError that has more 1 message" should "result in the same FormError" in {
    val fe1 = FormError("", Seq("message1", "message2"), "arg1")
    fe1.convert should be(fe1)
  }

  "converting a global FormError with a message that is just 'fieldError' followed by '.bob1.bob2' " should "result in the FormError(\"bob1\".\"bob1.bob2\")" in {
    val fe1 = FormError("", "fieldError|field1.field2|code1.code2", "arg1")
    val fe2 = FormError("field1.field2", "field1.field2.code1.code2", "arg1")
    fe1.convert should be(fe2)
  }
  
}
