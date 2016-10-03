val FINATRA_VERSION = "2.4.0"
val SPRAY_VERSION = "1.3.2"
val LOG_VERSION = "2.6.2"

lazy val root = (project in file(".")).
  settings(
    name := "Project-Eagle-Eye",
    version := "1.0",
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(
      "com.twitter" %% "finatra-http" % FINATRA_VERSION,
      "com.twitter" %% "finatra-slf4j" % FINATRA_VERSION
      //"io.spray" %%  "spray-json" % SPRAY_VERSION,
    ),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      "Twitter Maven" at "https://maven.twttr.com"
    )
  )