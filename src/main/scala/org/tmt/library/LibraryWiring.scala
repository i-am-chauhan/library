package org.tmt.library

import akka.http.scaladsl.server.Route
import csw.database.DatabaseServiceFactory
import csw.logging.client.scaladsl.LoggingSystemFactory
import esw.http.template.wiring.ServerWiring
import org.jooq.DSLContext
import org.tmt.library.core.impl.LibraryImpl
import org.tmt.library.http.LibraryRoute

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class LibraryWiring(val port: Option[Int]) extends ServerWiring {
  override val actorSystemName: String = "library-actor-system"

  LoggingSystemFactory.forTestingOnly()(actorRuntime.typedSystem)

  private val databaseServiceFactory = new DatabaseServiceFactory(actorRuntime.typedSystem)
  private val dslContext: DSLContext =
    Await.result(databaseServiceFactory.makeDsl(cswServices.locationService, "library"), 10.seconds)

  logger.info("started")
  lazy val libraryImpl = new LibraryImpl(dslContext)(actorRuntime.ec)

  import actorRuntime.ec
  override lazy val routes: Route = new LibraryRoute(libraryImpl).route
}
