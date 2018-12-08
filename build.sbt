/*
enablePlugins(ScalaJSPlugin)

val monocleVersion = "1.5.0-cats"

lazy val puppeteer = crossProject.in(file(".")).
  settings(
    name := "scala-rx-puppeteer",
    normalizedName := "scala-rx-puppeteer",
    version := "0.1",
    organization := "com.github.fbaierl",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    crossScalaVersions := Seq("2.11.12", "2.12.4"),
    scalaVersion := crossScalaVersions.value.last,
    homepage := Some(url("https://github.com/fbaierl/scala-rx-puppeteer")),
    licenses += ("MIT License", url("http://www.opensource.org/licenses/mit-license.php")),
    scmInfo := Some(ScmInfo(
      url("https://github.com/fbaierl/scala-rx-puppeteer"),
      "scm:git:git@github.com/fbaierl/scala-rx-puppeteer.git",
      Some("scm:git:git@github.com/fbaierl/scala-rx-puppeteer.git"))),
    publishMavenStyle := true,
    isSnapshot := false,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := <developers>
      <developer>
        <id>fbaierl</id>
        <name>Florian Baierl</name>
        <url>https://github.com/fbaierl</url>
      </developer>
    </developers>,
    pomIncludeRepository := { _ => false }
  ).
  jvmSettings(
    libraryDependencies += "com.lihaoyi" %% "scalarx" % "0.4.0",
    libraryDependencies += "com.github.fbaierl" %% "scala-tarjan" % "0.1.2"
  ).
  jsSettings(
    libraryDependencies += "com.lihaoyi" %%% "scalarx" % "0.4.0",
    libraryDependencies += "com.github.fbaierl" %%% "scala-tarjan" % "0.1.2"
  )

lazy val puppeteerJVM = puppeteer.jvm
lazy val puppeteerJS = puppeteer.js

*/

import sbt.Keys.libraryDependencies

name := "scala-rx-puppeteer"

scalaVersion in ThisBuild := "2.12.4"

lazy val root = project.in(file(".")).
  aggregate(rxPuppeteerJS, rxPuppeteerJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val rxPuppeteer = crossProject.in(file(".")).
  settings(
    name := "scala-rx-puppeteer",
    version := "0.1.1",
    organization := "com.github.fbaierl",
    scalaVersion := "2.12.4",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    normalizedName := "scala-rx-puppeteer",
    crossScalaVersions := Seq("2.10.6", "2.11.11, 2.12.2"),
    homepage := Some(url("https://github.com/fbaierl/scala-rx-puppeteer")),
    licenses += ("MIT License", url("http://www.opensource.org/licenses/mit-license.php")),
    scmInfo := Some(ScmInfo(
      url("https://github.com/fbaierl/scala-rx-puppeteer"),
      "scm:git:git@github.com/fbaierl/scala-rx-puppeteer.git",
      Some("scm:git:git@github.com/fbaierl/scala-rx-puppeteer.git"))),
    publishMavenStyle := true,
    isSnapshot := false,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra :=
      <developers>
        <developer>
          <id>fbaierl</id>
          <name>Florian Baierl</name>
          <url>https://github.com/fbaierl</url>
        </developer>
      </developers>,
    pomIncludeRepository := { _ => false }
  ).
  jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided",
    libraryDependencies += "com.lihaoyi" %% "scalarx" % "0.4.0",
    libraryDependencies += "com.github.fbaierl" %% "scala-tarjan" % "0.1.2"
  ).
  jsSettings(
    libraryDependencies += "com.lihaoyi" %%% "scalarx" % "0.4.0",
    libraryDependencies += "com.github.fbaierl" %%% "scala-tarjan" % "0.1.2"
  )

lazy val rxPuppeteerJVM = rxPuppeteer.jvm
lazy val rxPuppeteerJS = rxPuppeteer.js


