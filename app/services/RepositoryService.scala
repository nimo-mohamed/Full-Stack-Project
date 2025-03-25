package services

import models.{APIError, DataModel}
import org.mongodb.scala.result
import repositories.DataRepository

import javax.inject.Inject
import scala.concurrent.Future


class RepositoryService @Inject()(dataRepository: DataRepository) {

  def index(): Future[Either[APIError, Seq[DataModel]]] = dataRepository.index()
  def read(id: String): Future[Either[APIError, DataModel]] = dataRepository.read(id)
  def create(book:DataModel): Future[Either[APIError, DataModel]] = dataRepository.create(book)
  def update(id: String, book: DataModel): Future[Either[APIError, result.UpdateResult]] = dataRepository.update(id, book)
  def delete(id: String): Future[Either [models.APIError, result.DeleteResult]] = dataRepository.delete(id)
  def findByName(name: String): Future[Either[APIError, DataModel]] = dataRepository.findByName(name)
}
