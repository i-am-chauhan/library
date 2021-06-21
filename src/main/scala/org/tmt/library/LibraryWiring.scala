package org.tmt.library

import akka.http.scaladsl.server.Route
import csw.database.DatabaseServiceFactory
import csw.logging.api.scaladsl.Logger
import csw.logging.client.scaladsl.LoggerFactory
import csw.prefix.models.Prefix
import csw.prefix.models.Subsystem.ESW
import esw.http.template.wiring.ServerWiring
import org.jooq.DSLContext
import org.tmt.library.core.impl.LibraryImpl
import org.tmt.library.http.LibraryRoute

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class LibraryWiring(val port: Option[Int]) extends ServerWiring {

  override lazy val actorSystemName: String = "library-actor-system"
  import actorRuntime.ec

  private lazy val databaseServiceFactory = new DatabaseServiceFactory(actorRuntime.typedSystem)
  private val dbName                      = settings.config.getString("dbName")
  private val dbUsernameHolder            = settings.config.getString("dbUsernameHolder")
  private val dbPasswordHolder            = settings.config.getString("dbPasswordHolder")
  private lazy val dslContext: DSLContext =
    Await.result(
      databaseServiceFactory.makeDsl(cswServices.locationService, dbName, dbUsernameHolder, dbPasswordHolder),
      10.seconds
    )

  logger.info(s"Successfully connected to the database ${dbName} and stared the server")
  private lazy val libraryImpl    = new LibraryImpl(dslContext)
  override lazy val routes: Route = new LibraryRoute(libraryImpl).route
}
