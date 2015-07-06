name := "piskvorky"

version := "1.0"

scalaVersion := "2.11.6"


// Add managed dependency on ScalaFX library
libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx"        % "8.0.0-R4",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.2.2"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)

// Add dependency on JavaFX library (only for Java 7)
unmanagedJars in Compile += Attributed.blank(file(scala.util.Properties.javaHome) / "/lib/jfxrt.jar")

// Fork a new JVM for 'run' and 'test:run'
fork := true
