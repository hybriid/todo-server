// build.sc
import mill._, scalalib._

object todoServer extends ScalaModule{
  def scalaVersion = "2.13.2"
  object test extends Tests{
    def ivyDeps = Agg(
      ivy"io.getquill::quill-jdbc:3.5.2",
      ivy"org.postgresql:postgresql:42.2.8",
      ivy"com.opentable.components:otj-pg-embedded:0.13.1",
      ivy"com.lihaoyi::cask:0.7.4",
      ivy"com.lihaoyi::scalatags:0.9.1"
    )
    def testFrameworks = Seq("utest.runner.Framework")
  }
}
