lazy val root: Project = project.in(file(".")).dependsOn(RootProject(latestSbtUmbrella))
lazy val latestSbtUmbrella = uri("git://github.com/kamon-io/kamon-sbt-umbrella.git")
