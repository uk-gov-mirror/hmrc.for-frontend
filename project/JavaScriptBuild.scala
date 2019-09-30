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
      val npmResult = Grunt.npmProcess(uiDirectory.value, "install").run().exitValue()
      if(npmResult != 0) {
        throw new IllegalStateException("npm install failed")
      }else {
        npmResult
      }
    },
    gruntBuild := {
      val gruntResult = Grunt.gruntProcess(uiDirectory.value, "generate-assets").run().exitValue()
      if(gruntResult != 0) {
        throw new IllegalStateException("grunt build failed!")
      }else {
        gruntResult
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
