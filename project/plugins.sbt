addSbtPlugin("com.lucidchart"    % "sbt-scalafmt"    % "1.10")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "3.0.1")
addSbtPlugin("com.typesafe.sbt"   % "sbt-native-packager" % "1.2.2")
libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25" // Needed by sbt-git
