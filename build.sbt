name := "pisqworks"
version := "1.0"
scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.scalafx" % "scalafxml-core-sfx8_2.11" % "0.2.2",
  "junit" % "junit" % "4.11" % "test"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)

// Fork a new JVM for 'run' and 'test:run'
fork := true
