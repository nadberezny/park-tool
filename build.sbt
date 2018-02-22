lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.8"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.nadberezny",
      scalaVersion    := "2.12.4"
    )),
    mainClass in Compile  := Some("com.nadberezny.parktool.ParkToolApp"),
    name                  := "ParkTool",
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
      "com.typesafe.akka" %% "akka-cluster" % "2.5.9"
    )
  )

enablePlugins(AshScriptPlugin) // Since we're using alpine image we need a way to fire bash scripts
enablePlugins(DockerPlugin)
enablePlugins(JavaAppPackaging)

