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

package controllers

import play.api.Play.current
import play.api.i18n.Lang
import play.api.mvc._
import uk.gov.hmrc.play.language.LanguageController
import play.api.libs.concurrent.Execution.Implicits.defaultContext


object CustomLanguageController extends LanguageController {

  def showEnglish = Action.async { implicit request =>
    switchToLanguage("english")(request).map(_.withHeaders(LOCATION -> routes.LoginController.show().url))
  }

  def showWelsh = Action.async { implicit request =>
    switchToLanguage("cymraeg")(request).map(_.withHeaders(LOCATION -> routes.LoginController.show().url))
  }

  def langToCall(lang: String): Call = controllers.routes.CustomLanguageController.switchToLanguage(lang)

  override protected def fallbackURL: String = current.configuration.getString("language.fallbackUrl").getOrElse("/")

  override def languageMap: Map[String, Lang] = Map("english" -> Lang("en"),
    "cymraeg" -> Lang("cy"))
}
