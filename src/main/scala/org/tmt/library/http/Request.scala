package org.tmt.library.http

sealed trait Request

object Request {
  case class InsertBook(title: String, author: String) extends Request
}
