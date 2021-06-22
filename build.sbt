name := "library"

version := "0.0.1"

scalaVersion := "2.13.5"

resolvers += "jitpack" at "https://jitpack.io"
resolvers += "bintray" at "https://jcenter.bintray.com"
fork := true
run / javaOptions += "-Dcsw-networks.hostname.automatic=on"
envVars in Test := Map("DB_READ_USERNAME" -> "postgres", "DB_READ_PASSWORD" -> "postgres")

libraryDependencies ++= Seq(
  Libs.`esw-http-template-wiring` % "compile->compile;test->test",
  Libs.`csw-database`,
  Libs.`embedded-keycloak`        % Test,
  Libs.`scalatest`                % Test,
  Libs.`akka-http-testkit`        % Test,
  Libs.`mockito-scala`            % Test,
  Libs.`akka-actor-testkit-typed` % Test,
  Libs.`akka-stream-testkit`      % Test,
  Libs.`otj-pg-embedded`          % Test
)

enablePlugins(FlywayPlugin)
// flyway settings
flywayUrl := "jdbc:postgresql://localhost:5432/library"
flywayUser := "postgres"
flywayPassword := "postgres"
flywayLocations += "db/migration"
flywayBaselineOnMigrate := true
flywayBaselineVersion := "0"
