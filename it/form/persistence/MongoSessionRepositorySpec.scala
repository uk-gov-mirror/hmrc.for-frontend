package form.persistence

import java.util.UUID

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import MongoSessionRepositorySpecData._
import org.scalatest.OptionValues

import scala.concurrent.ExecutionContext

class MongoSessionRepositorySpec extends PlaySpec with OptionValues  with FutureAwaits
  with DefaultAwaitTimeout with GuiceOneAppPerSuite {

  def mongoSessionrepository() = app.injector.instanceOf[MongoSessionRepository]

  implicit def ec = app.injector.instanceOf[ExecutionContext]


  "Mongo session repository" should {
    "Store data in mongo" in {

      val cacheId = UUID.randomUUID().toString
      val formId = "formId"
      val testObject = MongoSessionRepositorySpecData(name = "John", buildingNumber = 100)

      await(mongoSessionrepository().cache(cacheId, formId, testObject)(format, ec))

      val res = await(mongoSessionrepository().fetchAndGetEntry[MongoSessionRepositorySpecData](cacheId, formId)(format, ec))
      res.value mustBe(testObject)

    }

    "store multiple pages in mongo not afection other" in {
      val cacheId = UUID.randomUUID().toString
      val page1 = "page1"
      val page2 = "page2"
      val testObject1 = MongoSessionRepositorySpecData(name = "John", buildingNumber = 100)
      val testObject2 = MongoSessionRepositorySpecData(name = "Peter", buildingNumber = -200)

      await(mongoSessionrepository().cache(cacheId, page1, testObject1)(format, ec))
      await(mongoSessionrepository().cache(cacheId, page2, testObject2)(format, ec))


      val testObjectFromDatabase1 = await(mongoSessionrepository().fetchAndGetEntry[MongoSessionRepositorySpecData](cacheId, page1)(format, ec))
      testObjectFromDatabase1.value mustBe(testObject1)
      val testObjectFromDatabase2 = await(mongoSessionrepository().fetchAndGetEntry[MongoSessionRepositorySpecData](cacheId, page2)(format, ec))
      testObjectFromDatabase2.value mustBe(testObject2)
    }
  }

}

case class MongoSessionRepositorySpecData(name: String, buildingNumber: Int)

object MongoSessionRepositorySpecData {

  implicit val format = Json.format[MongoSessionRepositorySpecData]

}