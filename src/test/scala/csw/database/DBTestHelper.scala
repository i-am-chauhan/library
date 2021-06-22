package csw.database

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import csw.database.DatabaseServiceFactory.{ReadPasswordHolder, ReadUsernameHolder}
import org.jooq.DSLContext
import org.scalatest.concurrent.ScalaFutures

import java.io.File
import java.nio.file.{Files, Path, Paths}
import scala.concurrent.duration.DurationInt

object DBTestHelper extends ScalaFutures {

  def copyToTmp(fileName: String, suffix: String = ".tmp"): Path = {
    val resourceStream = getClass.getResourceAsStream(fileName)
    try {
      val tempFile = File.createTempFile(String.valueOf(resourceStream.hashCode), suffix)
      tempFile.deleteOnExit()
      Files.write(tempFile.toPath, resourceStream.readAllBytes())
    }
    finally {
      resourceStream.close()
    }
  }

  def postgres(port: Int): EmbeddedPostgres =
    EmbeddedPostgres.builder
      .setServerConfig("listen_addresses", "*")
      .setServerConfig("hba_file", copyToTmp("/pg_hba.conf").toString)
      .setDataDirectory(Paths.get("/tmp/postgresDataDir"))
      .setCleanDataDirectory(true)
      .setPort(port)
      .start

  def dbServiceFactory(system: ActorSystem[SpawnProtocol.Command]) =
    new DatabaseServiceFactory(system, Map(ReadUsernameHolder -> "postgres", ReadPasswordHolder -> "postgres"))

  def dslContext(system: ActorSystem[SpawnProtocol.Command], port: Int): DSLContext = {
    implicit val patienceConfig: PatienceConfig = PatienceConfig(5.seconds)
    dbServiceFactory(system)
      .makeDsl(port)
      .futureValue
  }
}
