/*
 * Copyright 2022 HM Revenue & Customs
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

package models.serviceContracts.submissions

case class Landlord(
  landlordFullName: Option[String], // TODO: Change to landlordFullName: String in 2 months after deploying to production for-frontend 3.595.0 (VOA-3413 RALD – Section 5 - Landlord’s name to become a mandatory field)
  landlordAddress: Option[Address],
  landlordConnectionType: LandlordConnectionType,
  landlordConnectText: Option[String])
