import sbt._
import sbt.Keys._
import bintray.Plugin.bintraySettings
import bintray.Keys._

object ArcheryBuild extends Build {

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "1.9.1"
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.10.0"

  override lazy val settings = super.settings ++ Seq(
    organization := "com.meetup",
    scalaVersion := "2.10.2",
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    homepage := Some(url("http://github.com/meetup/archery")),
    version := "0.1.0",

    scalacOptions ++= Seq(
      //"-no-specialization", // use this to build non-specialized jars
      "-Yinline-warnings",
      "-deprecation",
      "-unchecked",
      "-optimize",
      "-language:_",
      "-feature"
    )
  ) ++ publishSettings

  lazy val publishSettings = Seq(
    bintrayOrganization in bintray := Some("meetup")
  )

  lazy val noPublish = Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )

  // core project
  lazy val core = Project("core", file("core")).
    settings(coreSettings: _*)

  lazy val coreSettings = Seq(
    name := "archery",
    libraryDependencies ++= Seq(
      scalaTest % "test",
      scalaCheck % "test"
    )
  ) ++ bintraySettings

  // benchmark project
  lazy val benchmark = Project("benchmark", file("benchmark")).
    settings(benchmarkSettings: _*).
    dependsOn(core)

  lazy val benchmarkSettings = Seq(
    name := "archery-benchmark",

    fork in run := true,

    javaOptions in run += "-Xmx4G",

    libraryDependencies ++= Seq(
      "ichi.bench" % "thyme" % "0.1.1" from "http://plastic-idolatry.com/jars/thyme-0.1.1.jar"
    )
  ) ++ noPublish

 // server project
 lazy val server = Project("server", file("server")).
    settings(serverSettings: _*).
    dependsOn(core)

  lazy val serverSettings = Seq(
    name := "archery-server",
    libraryDependencies ++= Seq(
      "io.netty" % "netty-all" % "4.0.15.Final",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.1.3",
      scalaTest % "test",
      scalaCheck % "test"
    )
  )

  // aggregate top-level project
  lazy val aggregate = Project("aggregate", file(".")).
    aggregate(core, benchmark, server).
    settings(aggregateSettings: _*)

  lazy val aggregateSettings = Seq(
    name := "archery-aggregate"
  ) ++ noPublish
}
