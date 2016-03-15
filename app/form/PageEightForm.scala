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

package form

import models.serviceContracts.submissions.RentAgreement
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.validation.{Constraint, Valid}
import uk.gov.voa.play.form.ConditionalMappings._


object PageEightForm {

  import MappingSupport._

  private val pageEightConstraint: Constraint[RentAgreement] = Constraint("constraints.pageEightData")({
    pageEightData => {
      def validateContainsRentReview() = {
        def checkRentReviewDetails() = {
          createFieldConstraintFor(
            cond = pageEightData.notReviewRentFixed.isDefined,
            code = Errors.required,
            fields = Seq("notReviewRentFixed"))
        }

        if (pageEightData.wasRentFixedBetween == false) {
          checkRentReviewDetails()
        } else {
          Valid
        }
      }
      validateContainsRentReview()
    }
  })

  val pageEightForm = Form(mapping(
    "wasRentFixedBetween" -> mandatoryBoolean,
    "notReviewRentFixed" -> mandatoryIfFalse("wasRentFixedBetween", notReviewRentFixedTypeMapping),
    "rentSetByType" -> rentSetByTypeMapping
  )(RentAgreement.apply)(RentAgreement.unapply))
}
 
