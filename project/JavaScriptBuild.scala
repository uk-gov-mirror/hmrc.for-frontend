import sbt._
import com.typesafe.sbt.packager.Keys._
import sbt.Keys._
import scala.sys.process._

object JavaScriptBuild {

  import play.sbt.PlayImport.PlayKeys._

  val uiDirectory = SettingKey[File]("ui-directory")
  val gruntBuild = TaskKey[Int]("grunt-build")
  val gruntWatch = TaskKey[Int]("grunt-watch")
  val npmInstall = TaskKey[Int]("npm-install")

  val javaScriptUiSettings = Seq(
    uiDirectory := {
      (baseDirectory in Compile).value
    },
    commands ++=  Seq(Grunt.gruntCommand(uiDirectory.value), npmCommand(uiDirectory.value)),
    npmInstall := {
      (1 to 3).map(_ => 0).find(_ => {
        val exitValue = Grunt.npmProcess(uiDirectory.value, "install").run().exitValue()
        exitValue == 0
      }).getOrElse {
        throw new IllegalStateException("npm install failed.")
      }
    },
    gruntBuild := {
      (1 to 3).map(_ => 0).find(_ => {
        val exitValue = Grunt.gruntProcess(uiDirectory.value, "generate-assets").run().exitValue()
        exitValue == 0
      }).getOrElse {
        throw new IllegalStateException("grunt build failed!")
      }
    },
    gruntWatch := Grunt.gruntProcess(uiDirectory.value, "watch").run().exitValue(),
    gruntBuild := (gruntBuild dependsOn npmInstall).value,
    dist := (dist dependsOn gruntBuild).value,
    playRunHooks += Grunt(uiDirectory.value)
  )

  def npmCommand(base: File) = Command.args("npm", "<npm-command>") { (state, args) =>
    Process("npm" :: args.toList, base) !;
    state
  }
}
