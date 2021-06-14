package org.tmt.library.core.impl

import csw.database.scaladsl.JooqExtentions.{RichQuery, RichResultQuery}
import org.jooq.DSLContext
import org.tmt.library.core.models.Book
import org.tmt.library.core.service.LibraryService

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class LibraryImpl(dsl: DSLContext)(implicit ec: ExecutionContext) extends LibraryService {
  override def getBooks: Future[List[Book]] = dsl.resultQuery("SELECT * from BOOKS").fetchAsyncScala[Book]

  override def getBooks(title: String): Future[List[Book]] =
    dsl.resultQuery(s"SELECT * from BOOKS WHERE UPPER(title) LIKE UPPER('$title%')").fetchAsyncScala[Book]

  override def insertBook(title: String, author: String): Future[String] = {
    val id = UUID.randomUUID().toString
    dsl
      .query(s"INSERT INTO BOOKS (id, title, author, available) values ('$id', '$title', '$author', TRUE)")
      .executeAsyncScala()
      .map(_ => id)
  }
}
