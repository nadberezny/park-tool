lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.8"
lazy val slickVersion = "3.2.1"
lazy val slickJodaMapperVersion = "2.3.0"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.nadberezny",
      scalaVersion    := "2.12.4"
    )),
    mainClass in Compile  := Some("com.nadberezny.parkcalculator.ParkCalculatorApp"),
    name                  := "ParkCalculator",
    dockerBaseImage       := "openjdk:jre-alpine",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test,
      "com.github.nscala-time" %% "nscala-time" % "2.18.0",
      "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster" % "2.5.9",
      "com.typesafe.play" %% "play-json" % "2.6.0-M7",
      "com.typesafe.akka" %% "akka-slf4j" % "2.5.9",
      "ch.qos.logback" % "logback-classic" % "1.2.3", // SLF4J
      "com.typesafe.slick" %% "slick" % slickVersion,
      "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
      "org.postgresql" % "postgresql" % "42.2.1",
      "com.github.tototoshi" %% "slick-joda-mapper" % slickJodaMapperVersion,
      "joda-time" % "joda-time" % "2.7",
      "org.joda" % "joda-convert" % "1.7",
      "com.chuusai" %% "shapeless" % "2.3.3",
      "org.typelevel" %% "cats-core" % "1.0.1"
    )
  )

enablePlugins(AshScriptPlugin) // Since we're using alpine image we need a way to fire bash scripts
enablePlugins(DockerPlugin)
enablePlugins(JavaAppPackaging)

scalacOptions += "-Ypartial-unification"

resolvers += Resolver.bintrayRepo("cakesolutions", "maven")
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)
