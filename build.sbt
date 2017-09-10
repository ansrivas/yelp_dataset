// *****************************************************************************
// Projects
// *****************************************************************************
lazy val currentYear: Int = java.time.OffsetDateTime.now().getYear

resolvers +=  "Typesafe" at "http://repo.typesafe.com/typesafe/releases/"

lazy val yelp =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .enablePlugins( JavaAppPackaging)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.scalaCheck % Test,
        library.scalaTest  % Test,
        library.sparkTestingBase % Test,
        library.sparkCore,
        library.sparkSql,
        library.sparkHive,
        library.typesafeConfig
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val scalaCheck = "1.13.5"
      val scalaTest  = "3.0.3"
      val sparkV = "2.2.0"
      val cassandraSparkV = "2.0.5"
      val cassandraJavaV = "3.3.0"
      val sparkTestingBaseV = "2.2.0_0.7.4"
      val typesafeConfigV = "1.3.1"
      val commonsCompressV =  "1.14"
    }
    val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
    val scalaTest  = "org.scalatest"  %% "scalatest"  % Version.scalaTest
    val sparkCore = "org.apache.spark" %% "spark-core" % Version.sparkV
    val sparkSql = "org.apache.spark" %% "spark-sql" % Version.sparkV
    val sparkHive= "org.apache.spark" %% "spark-hive" % Version.sparkV
    val sparkTestingBase = "com.holdenkarau" %% "spark-testing-base" % Version.sparkTestingBaseV
    val typesafeConfig = "com.typesafe" % "config" % Version.typesafeConfigV
   }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  scalafmtSettings++
  assemblySettings

lazy val commonSettings =
  Seq(
    scalaVersion := "2.11.11",
    organization := "default",
    organizationName := "com.ansrivas",
    startYear := Some(currentYear),
    licenses += ("MIT", url("https://opensource.org/licenses/MIT")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8"
    ),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value),
    shellPrompt in ThisBuild := { state =>
      val project = Project.extract(state).currentRef.project
      s"[$project]> "
    },
    parallelExecution in Test := false,
    fork in Test := true,
    javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")
)


lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtOnCompile.in(Sbt) := false,
    scalafmtVersion := "1.1.0"
  )

lazy val assemblySettings =
  Seq(
    assemblyJarName in assembly := "main.jar",
    mainClass in (Compile, packageBin) := Some("com.ansrivas.Main"),
//    mainClass in assembly := Some("com.ansrivas.Main"),
    assemblyOutputPath in assembly := file( "dist/" + (assemblyJarName in assembly).value )
  )

assemblyMergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")     => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$") => MergeStrategy.discard
  // Incase log4j.properties creates an issue during assembly then uncomment this line
  // case "log4j.properties"                             => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") =>
    MergeStrategy.filterDistinctLines
  case "reference.conf" => MergeStrategy.concat
  case _                => MergeStrategy.first
}
