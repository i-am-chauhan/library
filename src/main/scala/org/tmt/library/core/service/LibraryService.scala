package org.tmt.library.core.service

import org.tmt.library.core.models.Book

import scala.concurrent.Future

trait LibraryService {
  def getBooks: Future[List[Book]]
  def getBooks(title: String): Future[List[Book]]
  def insertBook(title: String, author:String): Future[String]
}
