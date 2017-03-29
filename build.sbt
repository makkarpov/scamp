publishArtifact := false

enablePlugins(CrossPerProjectPlugin)

val commonSettings = Seq(
  organization := "ru.makkarpov.scamp",
  version := "1.0",

  scalaVersion := "2.11.7",

  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

lazy val macros = project
  .settings(commonSettings)
  .settings(
    name := "scamp-macros",

    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.4.17",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
  )

lazy val root = project.in(file("."))
  .settings(commonSettings)
  .settings(
    name := "scamp",

    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % "2.4.17"
    )
  )
  .dependsOn(macros)

lazy val misc = project
  .settings(commonSettings)
  .settings(
    name := "scamp-misc"
  )
  .dependsOn(root)