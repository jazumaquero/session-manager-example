name := "session-manager-example"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.4.17"
  val akkaHttpV = "10.0.4"
  val scalaCacheV = "0.9.3"
  val scalaTestV = "3.0.1"

  Seq(
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-persistence" % akkaV,
    "com.typesafe.akka"   %%  "akka-http"    % akkaHttpV,
    "com.typesafe.akka"   %%  "akka-http-core"    % akkaHttpV,
    "com.typesafe.akka"	  %%  "akka-http-jackson"	% akkaHttpV,
    "com.github.cb372"    %%  "scalacache-guava" % scalaCacheV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "com.typesafe.akka"   %%  "akka-http-testkit" % akkaHttpV,
    "org.scalatest"       %%  "scalatest"     % scalaTestV % "test",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
  )
}
javaOptions in Test += "-Dconfig.file=application.test.conf"

mainClass in assembly := Some("com.zcia.example.session.manager.SessionManagerServer")
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

val meta = """META.INF(.)*""".r
assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case n if n.startsWith("reference.conf") => MergeStrategy.concat
  case n if n.endsWith(".conf") => MergeStrategy.concat
  case meta(_) => MergeStrategy.discard
  case x => MergeStrategy.first
}