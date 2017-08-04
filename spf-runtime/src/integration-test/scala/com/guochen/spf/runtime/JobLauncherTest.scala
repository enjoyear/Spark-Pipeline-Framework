package com.guochen.spf.runtime

import java.io.File

import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JobLauncherTest extends FlatSpec with Matchers {
  "SampleScalaIntegrationTest" should "be executed" in {
    val config = ConfigFactory.parseFile(
      new File(getClass.getResource("file-ingestion.conf").getPath))

    new JobLauncher(config).start()
  }
}
