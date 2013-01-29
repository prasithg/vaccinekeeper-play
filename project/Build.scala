import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "vaccinekeeper"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
    	"org.mongodb" % "mongo-java-driver" % "2.10.1",
		"net.vz.mongodb.jackson" %% "play-mongo-jackson-mapper" % "1.0.0",
		"net.sf.opencsv" % "opencsv" % "2.3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
		resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"
		
    )

}
