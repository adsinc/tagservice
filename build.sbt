name := "thrift"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.thrift" % "libthrift" % "0.9.3",
  "com.twitter" %% "scrooge-core" % "4.6.0",
  "com.twitter" %% "finagle-thrift" % "6.34.0"
)

//unmanagedSourceDirectories in Compile += baseDirectory.value / "target" / "scala-2.11" / "src_managed"