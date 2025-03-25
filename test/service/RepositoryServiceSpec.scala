package service

import baseSpec.BaseSpec
import connectors.LibraryConnector
import models.{APIError, DataModel}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import repositories.DataRepositoryTrait
import services.RepositoryService
import org.mongodb.scala.result

import scala.concurrent.{ExecutionContext, Future}

class RepositoryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite {
  val mockDataRepo = mock[DataRepositoryTrait]
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

        whenReady(testRepoService.create(dataModel)) { result =>
          result shouldBe Right(dataModel)
        }
      }
    }
    "return a Left" when {
      "dataRepository create returns a Left" in {
        val apiError = APIError.BadAPIResponse(500, s"An error occurred when trying to add book with id: S{dataModel._id}")
        (mockDataRepo.create(_: DataModel))
          .expects(dataModel)
          .returning(Future.successful(Left(apiError)))
          .once()

        whenReady(testRepoService.create(dataModel)) { result =>
          result shouldBe Left(apiError)
        }
      }
    }
  }

  "read" should {
    "return a Right" when {
      "dataRepository read returns a Right" in {
        val id = dataModel._id

        (mockDataRepo.read(_: String))
          .expects(id)
          .returning(Future(Right(dataModel)))
          .once()

        whenReady(testRepoService.read(id)) { result =>
          result shouldBe Right(dataModel)
        }
      }
    }

    "return a Left" when {
      "dataRepository read returns a Left" in {
        val id = dataModel._id

        val apiError = APIError.BadAPIResponse(404, s"An error occurred when trying find a book with id: S{dataModel._id}")
        (mockDataRepo.read(_: String))
          .expects(id)
          .returning(Future.successful(Left(apiError)))
          .once()

        whenReady(testRepoService.read(id)) { result =>
          result shouldBe Left(apiError)
        }
      }
    }
  }


  "findByName" should {
    "return a Right" when {
      "dataRepository findByName returns a Right" in {
        val name = dataModel.name

        (mockDataRepo.findByName(_: String))
          .expects(name)
          .returning(Future(Right(dataModel)))
          .once()

        whenReady(testRepoService.findByName(name)) { result =>
          result shouldBe Right(dataModel)
        }
      }
    }

    "return a Left" when {
      "dataRepository read returns a Left" in {
        val name = dataModel.name

        val apiError = APIError.BadAPIResponse(404, s"An error occurred when trying to find a book with title: S{dataModel.name}")
        (mockDataRepo.findByName(_: String))
          .expects(name)
          .returning(Future.successful(Left(apiError)))
          .once()

        whenReady(testRepoService.findByName(name)) { result =>
          result shouldBe Left(apiError)
        }
      }
    }
  }

  "delete" should {
    "return a Right" when {
      "dataRepository delete returns a Right" in {
        val id = dataModel._id
        val deleteResult = mock[result.DeleteResult]

        (mockDataRepo.delete(_: String))
          .expects(id)
          .returning(Future(Right(deleteResult)))
          .once()

        whenReady(testRepoService.delete(id)) { result =>
          result shouldBe Right(deleteResult)
        }
      }
    }

    "return a Left" when {
      "dataRepository delete returns a Left" in {
        val id = dataModel._id

        val apiError = APIError.BadAPIResponse(404, s"An error occurred when trying to delete a book with this id: S{dataModel._id}")
        (mockDataRepo.delete(_: String))
          .expects(id)
          .returning(Future.successful(Left(apiError)))
          .once()

        whenReady(testRepoService.delete(id)) { result =>
          result shouldBe Left(apiError)
        }
      }
    }
  }


  "update" should {
    "return a Right" when {
      "dataRepository update returns a Right" in {
        val id = dataModel._id
        val book = dataModel
        val updateResult = mock[result.UpdateResult]

        (mockDataRepo.update(_: String, _: DataModel))
          .expects(id, book)
          .returning(Future(Right(updateResult)))
          .once()

        whenReady(testRepoService.update(id, book)) { result =>
          result shouldBe Right(updateResult)
        }
      }
    }

    "return a Left" when {
      "dataRepository update returns a Left" in {
        val id = dataModel._id
        val book = dataModel

        val apiError = APIError.BadAPIResponse(500, s"An error occurred when trying to update a book with id: S{dataModel._id}")
        (mockDataRepo.update(_: String, _: DataModel))
          .expects(id, book)
          .returning(Future.successful(Left(apiError)))
          .once()

        whenReady(testRepoService.update(id, book)) { result =>
          result shouldBe Left(apiError)
        }
      }
    }
  }

  "index" should {
    "return a Right" when {
      "dataRepository index returns a Right" in {
        val books = Seq(dataModel)

        (mockDataRepo.index _)
          .expects()
          .returning(Future.successful(Right(books)))
          .once()

        whenReady(testRepoService.index()) { result =>
          result shouldBe Right(books)
        }
      }
    }
    "return a Left" when {
      "dataRepository index returns a Left" in {
        val apiError = APIError.BadAPIResponse(500, s"An error occurred when trying to fetch the books.")
        (mockDataRepo.index _)
          .expects()
          .returning(Future.successful(Left(apiError)))
          .once()

        whenReady(testRepoService.index()) { result =>
          result shouldBe Left(apiError)
        }
      }
    }
  }


}
