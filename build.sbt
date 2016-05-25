import sbt.Keys._

lazy val commonSettings = Seq(
  organization := "com.productfoundry",
  version := "0.1.5",

  scalaVersion := "2.11.8",

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

  // Test execution
  parallelExecution in Test := false,
  fork in Test := true,

  // Resolvers
  resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",

  // Dependencies
  libraryDependencies ++= Seq(
    "com.typesafe.play"      %% "play"                              % "2.5.3"    % "provided",
    "com.typesafe.play"      %% "play-test"                         % "2.5.3"    % "test",
    "org.scalatest"          %% "scalatest"                         % "2.2.4"    % "test",
    "org.scalacheck"         %% "scalacheck"                        % "1.12.2"   % "test"
  )
)

lazy val bintraySettings = Seq(
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  bintrayOrganization := Some("productfoundry")
)

lazy val root = (project in file("."))
  .enablePlugins(SbtTwirl)
  .settings(commonSettings: _*)
  .settings(bintraySettings: _*)
  .settings(
    name := "hal-scala"
  )
