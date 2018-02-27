scalaVersion in ThisBuild := "2.12.4"

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "KafkaTwitter",
  scalaVersion := "2.12.4"
)

val twitterVersion = "18.2.0"
val kafkaStreamsVersion = "0.10.0.0"
val hbcCoreVersion = "2.2.0"
val gsonVersion = "2.7"
val configVersion = "1.3.3"
val scalaLoggingVersion = "3.8.0"

lazy val kafkatwitter = (project in file("kafkatwitter")).
  settings(commonSettings: _*).
  settings(
    assemblyJarName in assembly := "kafkatwitter.jar"
  ).
  settings(
    resolvers ++= Seq(
      "twttr" at "http://maven.twttr.com/",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
    )
  ).settings(libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/edu.stanford.nlp/stanford-corenlp
  "org.json" % "json" % "20160212",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.8.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.8.0" classifier "models",
  "com.cybozu.labs" % "langdetect" % "1.1-20120112",
  "edu.stanford.nlp" % "stanford-parser" % "3.8.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.4",
  "com.twitter" %% "util-core" % twitterVersion,
  "com.twitter" % "hbc-core" % hbcCoreVersion,
  "org.apache.kafka" % "kafka-streams" % kafkaStreamsVersion,
  "com.google.code.gson" % "gson" % gsonVersion,
  "com.typesafe" % "config" % configVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
))

