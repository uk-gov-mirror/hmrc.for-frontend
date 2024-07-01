import sbt.{AutoPlugin, Setting}
import scoverage.ScoverageKeys

object CodeCoverageSettings extends AutoPlugin {

  override def trigger = allRequirements

  private val excludedPackages: Seq[String] = Seq(
    ".*\\.Reverse.*",
    ".*Routes",
    ".*RoutesPrefix",
    "views\\..*"
  )

  override lazy val projectSettings: Seq[Setting[?]] = Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 55.0,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )

}
