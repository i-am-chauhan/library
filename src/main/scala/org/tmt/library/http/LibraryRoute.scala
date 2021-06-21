package org.tmt.library.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.tmt.library.core.service.LibraryService
import org.tmt.library.http.Request.InsertBook

import scala.concurrent.ExecutionContext

class LibraryRoute(libraryService: LibraryService)(implicit
    ec: ExecutionContext
) extends HttpCodecs {

  def route: Route = {
    path("books") {
      post {
        entity(as[Request]) {
          case InsertBook(title, authorName) =>
            complete(libraryService.insertBook(title, authorName))
        }
      } ~
      get {
        complete(libraryService.getBooks)
      }
    }
  }
}
