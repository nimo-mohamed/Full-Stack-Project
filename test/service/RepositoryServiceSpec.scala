package service

import baseSpec.BaseSpec
import connectors.LibraryConnector
import models.{APIError, DataModel}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import repositories.MockRepository
import services.RepositoryService

import scala.concurrent.{ExecutionContext, Future}

class RepositoryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite {
  val mockDataRepo = mock[MockRepository]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testRepoService = new RepositoryService(mockDataRepo)

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  "create" should {
    "return a Right" when {
      "dataRepository create returns a Right" in {
        (mockDataRepo.create(_: DataModel))
          .expects(dataModel)
          .returning(Future(Right(dataModel)))
          .once()

        whenReady(testRepoService.create(dataModel)) { result =›
          result shouldBe Right(dataModel)
        }
      }
    }
    "return a Left" when {
      "dataRepository create returns a Left" in {
        val apiError = APIError.BadAPIResponse(500, s"An error occurred when trying to add book with id: S{dataModel._id}")
        (mockDataRepo.create(_: DataModel))
          .expects(dataModel)
          .returning(Future(Left(APIError)))
          .once()

        whenReady(testRepoService.create(dataModel)) { result =›
          result shouldBe Left(apiError)
        }
      }
    }
  }
