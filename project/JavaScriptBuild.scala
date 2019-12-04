import sbt._
import com.typesafe.sbt.packager.Keys._
import sbt.Keys._

object JavaScriptBuild {

  import play.sbt.PlayImport.PlayKeys._

  val uiDirectory = SettingKey[File]("ui-directory")
  val gruntBuild = TaskKey[Int]("grunt-build")
  val gruntWatch = TaskKey[Int]("grunt-watch")
  val npmInstall = TaskKey[Int]("npm-install")

  val javaScriptUiSettings = Seq(
    uiDirectory <<= (baseDirectory in Compile),

    commands <++= uiDirectory { base => Seq(Grunt.gruntCommand(base), npmCommand(base)) },

    npmInstall := {
      (1 to 3).map(_ => 0).find(_ => {
        val exitValue = Grunt.npmProcess(uiDirectory.value, "install").run().exitValue()
        exitValue == 0
      }).getOrElse {
        throw new IllegalStateException("grunt build failed!")
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

    gruntBuild <<= gruntBuild dependsOn npmInstall,

    dist <<= dist dependsOn gruntBuild,

    playRunHooks <+= uiDirectory.map(ui => Grunt(ui))
  )

  def npmCommand(base: File) = Command.args("npm", "<npm-command>") { (state, args) =>
    Process("npm" :: args.toList, base) !;
    state
  }
}
