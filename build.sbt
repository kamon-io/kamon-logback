/* =========================================================================================
 * Copyright © 2013-2018 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */


val kamonCore             = "io.kamon"        %%  "kamon-core"              % "1.1.3"
val kamonTestkit          = "io.kamon"        %%  "kamon-testkit"           % "1.1.3"
val kanelaScalaExtension  = "io.kamon"        %%  "kanela-scala-extension"  % "0.0.14"

val latestLogbackClassic  = "ch.qos.logback"  %   "logback-classic"         % "1.2.3"

resolvers += Resolver.bintrayRepo("kamon-io", "snapshots")

lazy val root = (project in file("."))
  .settings(Seq(
      name := "kamon-logback",
      scalaVersion := "2.12.5"))
  .enablePlugins(JavaAgent)
  .settings(javaAgents ++= resolveAgent)
  .settings(
    libraryDependencies ++=
      compileScope(kamonCore, latestLogbackClassic, kanelaScalaExtension) ++
      testScope(kamonTestkit, scalatest))

def resolveAgent: Seq[ModuleID] = {
  val agent = Option(System.getProperty("agent")).getOrElse("aspectj")
  if(agent.equalsIgnoreCase("kanela"))
    Seq("org.aspectj" % "aspectjweaver" % "1.9.1" % "compile", "io.kamon" % "kanela-agent" % "0.0.14" % "compile;test")
  else
    Seq("org.aspectj" % "aspectjweaver" % "1.9.1" % "compile;test", "io.kamon" % "kanela-agent" % "0.0.14" % "compile")
}
