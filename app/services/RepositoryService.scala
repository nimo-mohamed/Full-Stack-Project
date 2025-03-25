package services

import models.{APIError, DataModel}
import org.mongodb.scala.result
import repositories.{DataRepository, MockRepository}

import javax.inject.Inject
import scala.concurrent.Future


class RepositoryService @Inject()(mockRepository: MockRepository) {

  def index(): Future[Either[APIError, Seq[DataModel]]] = mockRepository.index()
  def read(id: String): Future[Either[APIError, DataModel]] = mockRepository.read(id)
  def create(book:DataModel): Future[Either[APIError, DataModel]] = mockRepository.create(book)
  def update(id: String, book: DataModel): Future[Either[APIError, result.UpdateResult]] = mockRepository.update(id, book)
  def delete(id: String): Future[Either [models.APIError, result.DeleteResult]] = mockRepository.delete(id)
  def findByName(name: String): Future[Either[APIError, DataModel]] = mockRepository.findByName(name)
}