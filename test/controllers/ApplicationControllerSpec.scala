package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers._

import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val TestApplicationController = new ApplicationController(
    component, repository, service)(executionContext)

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )


  // ✅ .index test
  "ApplicationController .index" should {
    "return OK" in {
      beforeEach()
      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe Status.OK
      afterEach()
    }

  }
  // ✅ .create tests
  "ApplicationController .create" should {

    "create a book in the database" in {
      beforeEach()
      val request: FakeRequest[JsValue] = buildPost("/api/create").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED
      afterEach()
    }
    "Return a bad request, 400" in {
      beforeEach()
      val invalidRequest: FakeRequest[JsValue] = buildPost("/api").withBody(Json.obj("invalid" -> "data"))
      val createdResult: Future[Result] = TestApplicationController.create()(invalidRequest)

      status(createdResult) shouldBe Status.BAD_REQUEST
      afterEach()
    }
  }

  // ✅ .read tests
  "ApplicationController .read" should {

    "find a book in the database by id" in {
      beforeEach()
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())
      println(readResult)
      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[DataModel] shouldBe dataModel
      afterEach()
    }
    "return a NOT_FOUND error when an id doesn't exist" in {
      beforeEach()
      val readResult: Future[Result] = TestApplicationController.read("grdt")(FakeRequest())
      status(readResult) shouldBe Status.NOT_FOUND
      afterEach()
    }
    //    "Return a NOT_FOUND, 404" in {
    //      beforeEach()
    //
    //      val nonExistentId = "non-existent-id"
    //      val request = buildGet("/api/$nonExistentId")
    //
    //      val readResult: Future[Result] = TestApplicationController.read(nonExistentId)(request)
    //
    //      status(readResult) shouldBe Status.NOT_FOUND
    //      contentAsString(readResult) should include("Unable to find data for ID")
    //
    //      afterEach()
    //    }
  }





  // ✅ .update tests
  "ApplicationController .update(id: String)" should {
    "update an existing book by id" in {
      beforeEach()
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED

      val updatedModel = dataModel.copy(name = "Updated Book Name")
      val updateRequest: FakeRequest[JsValue] = buildPut("/api/${dataModel._id}").withBody[JsValue](Json.toJson(updatedModel))


      val updatedResult: Future[Result] = TestApplicationController.update(dataModel._id)(updateRequest)
      status(updatedResult) shouldBe Status.ACCEPTED


      val updatedContent = contentAsJson(updatedResult).as[DataModel]
      updatedContent.name shouldBe "Updated Book Name"
      afterEach()
    }
    "Return a bad request, 400" in {
      beforeEach()
      val invalidRequest: FakeRequest[JsValue] = buildPut("/api/${dataModel._id}").withBody(Json.obj("description" -> 2))
      val updatedResult: Future[Result] = TestApplicationController.update(dataModel._id)(invalidRequest)

      status(updatedResult) shouldBe Status.BAD_REQUEST
      afterEach()
    }
  }

  // ✅ .delete tests
  "ApplicationController .delete(id: String)" should {
    "delete an existing book by id" in {
      beforeEach()
      val request: FakeRequest[JsValue] = buildPost("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED

      val deleteRequest: FakeRequest[JsValue] = buildDelete(s"/api/${dataModel._id}").withBody(Json.toJson(dataModel))

      val deletedResult: Future[Result] = TestApplicationController.delete(dataModel._id)(deleteRequest)
      println(deletedResult)
      status(deletedResult) shouldBe Status.ACCEPTED

      val confirmDelete: Future[Result] = TestApplicationController.read(dataModel._id)(FakeRequest())
      status(confirmDelete) shouldBe Status.NOT_FOUND

      afterEach()
    }
    "return a NOT_FOUND error when an id has been deleted" in {
      beforeEach()
      val readResult: Future[Result] = TestApplicationController.read("grdt")(FakeRequest())
      status(readResult) shouldBe Status.NOT_FOUND
      afterEach()
    }
  }

  override def beforeEach(): Unit = await(repository.deleteAll())

  override def afterEach(): Unit = await(repository.deleteAll())
}
