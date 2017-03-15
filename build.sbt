publishArtifact := false

enablePlugins(CrossPerProjectPlugin)

val commonSettings = Seq(
  organization := "ru.makkarpov",
  version := "1.0",

  scalaVersion := "2.11.7"
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
    name := "scamp"
  )
  .dependsOn(macros)