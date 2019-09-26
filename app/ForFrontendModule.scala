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

import ForFrontendModule.{FormDocumentRepositoryProvider}
import form.persistence.FormDocumentRepository
import javax.inject.Singleton
import javax.inject.Provider
import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}
import playconfig.{FormPersistence}


class ForFrontendModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[FormDocumentRepository].toProvider[FormDocumentRepositoryProvider].in[Singleton]
    )
  }
}


object ForFrontendModule {
  //TODO Remove after migration to dependency Injection

  class FormDocumentRepositoryProvider extends Provider[FormDocumentRepository] {
    override def get(): FormDocumentRepository = {
      FormPersistence.formDocumentRepository
    }
  }


}