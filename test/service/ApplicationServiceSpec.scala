package service

import baseSpec.BaseSpec
import cats.data.EitherT
import connectors.LibraryConnector
import models.APIError
import models.GoogleBook.Book
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json, OFormat}
import services.ApplicationService

import scala.concurrent.{ExecutionContext, Future}

class ApplicationServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite {
  val mockConnector = mock[LibraryConnector]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new ApplicationService(mockConnector)

  /**
   * VolumeInfo(title: String, description: String, pageCount: Int, industryIdentifier: Seq[Seq[IndustryIdentifier]], imageLink: ImageLink)
   */
  val gameOfThrones: JsValue = Json.obj(

    "volumeInfo" -> Json.obj(
      "title" -> "A Game of Thrones",
      "description" -> "The best book!!!",
      "pageCount" -> 100,
      "imageLink" -> Json.obj(
        "smallThumbnail" -> "http://example.com/small.jpg",
        "thumbnail" -> "http://example.com/thumbnail.jpg"
      ),
      "industryIdentifier" -> Json.arr(
        Json.obj("identifier" ->
          "9780553103540"
        )
      )
    )
  )

  "getGoogleBook" should {
    val url: String = "testUrl"

    "return a book" in {
      val expectedBook = gameOfThrones.as[Book]

      (mockConnector.get[Book](_: String)(_: OFormat[Book], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.rightT[Future, APIError](expectedBook))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { either =>
        either shouldBe Right(expectedBook)

      }
    }

    "return an error" in {

      val url: String = "testUrl"
      val error = APIError.BadAPIResponse(500, "API call failed")

      (mockConnector.get[Book](_: String)(_: OFormat[Book], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.leftT[Future, Book](error)) // How do we return an error?
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { either =>
        either shouldBe Left(error)
      }
    }
  }
}

