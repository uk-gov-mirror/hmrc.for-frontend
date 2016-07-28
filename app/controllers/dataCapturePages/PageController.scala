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

package controllers.dataCapturePages

import controllers._
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.frontend.controller.FrontendController

object PageController extends FrontendController {

  def showPage(pageNumber: Int): Action[AnyContent] =
    pageNumber match {
      case 0 => PageZeroController.show
      case 1 => PageOneController.show
      case 2 => PageTwoController.show
      case 3 => PageThreeController.show
      case 4 => PageFourController.show
      case 5 => PageFiveController.show
      case 6 => PageSixController.show
      case 7 => PageSevenController.show
      case 8 => PageEightController.show
      case 9 => PageNineController.show
      case 10 => PageTenController.show
      case 11 => PageElevenController.show
      case 12 => PageTwelveController.show
      case 13 => PageThirteenController.show
      case 14 => PageFourteenController.show
      case _ => Application.index
    }


  def savePage(pageNumber: Int): Action[AnyContent] =
    pageNumber match {
      case 0 => PageZeroController.save
      case 1 => PageOneController.save
      case 2 => PageTwoController.save
      case 3 => PageThreeController.save
      case 4 => PageFourController.save
      case 5 => PageFiveController.save
      case 6 => PageSixController.save
      case 7 => PageSevenController.save
      case 8 => PageEightController.save
      case 9 => PageNineController.save
      case 10 => PageTenController.save
      case 11 => PageElevenController.save
      case 12 => PageTwelveController.save
      case 13 => PageThirteenController.save
      case 14 => PageFourteenController.save
      case _ => Application.index
    }
}
