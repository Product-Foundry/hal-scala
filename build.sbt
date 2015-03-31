import bintray.Keys._
import com.typesafe.sbt.SbtGit._
import sbt.Keys._

lazy val commonSettings = Seq(
  organization := "com.productfoundry",
  version := "0.1.1",

  scalaVersion := "2.11.6",

  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Yinline",
    "-Xfuture"
  ),

  // Bintray
  repository in bintray := "maven",
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  bintrayOrganization in bintray := Some("productfoundry"),

  // Test execution
  parallelExecution in Test := false,
  fork in Test := true,

  // Resolvers
  resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",

  // Dependencies
  libraryDependencies ++= Seq(
    "com.typesafe.play"      %% "play"                              % "2.3.8",
    "com.typesafe.play"      %% "play-test"                         % "2.3.8"    % "test",
    "org.scalatest"          %% "scalatest"                         % "2.2.4"    % "test",
    "org.scalacheck"         %% "scalacheck"                        % "1.12.2"   % "test"
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "hal-scala"
  )
  .settings(bintrayPublishSettings: _*)
