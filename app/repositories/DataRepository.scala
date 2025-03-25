package repositories

import com.google.inject.ImplementedBy
import models.{APIError, DataModel}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.empty
import org.mongodb.scala.model._
import org.mongodb.scala.result
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[DataRepository])
trait MockRepository {
  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]]
  def create(book: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]]
  def read(id: String): Future[Either[APIError.BadAPIResponse, DataModel]]
  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, result.UpdateResult]]
  def delete(id: String): Future[Either[APIError.BadAPIResponse, result.DeleteResult]]
  def deleteAll(): Future[Unit]
  def findByName(name: String): Future[Either[APIError.BadAPIResponse, DataModel]]
}


@Singleton
class DataRepository @Inject()(
                                mongoComponent: MongoComponent
                              )(implicit ec: ExecutionContext) extends PlayMongoRepository[DataModel](
  collectionName = "dataModels",
  mongoComponent = mongoComponent,
  domainFormat = DataModel.formats,
  indexes = Seq(IndexModel(
    Indexes.ascending("_id")
  )),
  replaceIndexes = false
) {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] =
    collection.find().toFuture().map {
      case books: Seq[DataModel] => Right(books)
      case _ => Left(APIError.BadAPIResponse(404, "Books cannot be found"))
    }

  def create(book: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.insertOne(book).toFuture().map {
      case _ => Right(book)
      case _ => Left(APIError.BadAPIResponse(400, "This request is not valid"))
    }

  private def byID(id: String): Bson =
    Filters.and(
      Filters.equal("_id", id)
    )

  def read(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.find(byID(id)).headOption.map {
      case Some(data) => Right(data)
      case None => Left(APIError.BadAPIResponse(404, "This resource could not be found."))
    }


  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] =
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(true) //What happens when we set this to false? It will not create anything.
    ).toFuture().map(Right(_)).recover {
      case ex: Exception => Left(APIError.BadAPIResponse(500, s"An error occurred: ${ex.getMessage}"))
    }

  def delete(id: String): Future[Either[APIError.BadAPIResponse, result.DeleteResult]] =
    collection.deleteOne(
      filter = byID(id)
    ).toFuture().map(Right(_)).recover {
      case ex: Exception => Left(APIError.BadAPIResponse(404, s"An error occurred: ${ex.getMessage}"))
    }

  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) //Hint: needed for tests

  private def byName(name: String): Bson =
    Filters.and(
      Filters.equal("name", name)
    )

  def findByName(name: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.find(byName(name)).headOption.map {
      case Some(data) => Right(data)
      case None => Left(APIError.BadAPIResponse(404, "This resource could not be found."))
    }
}
