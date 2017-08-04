package com.guochen.spf

import java.io.File

import com.guochen.spf.runtime.JobLauncher
import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SampleScalaIntegrationTest extends FlatSpec with Matchers {
  "SampleScalaIntegrationTest" should "be executed" in {
    val config = ConfigFactory.parseFile(new File("/Users/chguo/repos/enjoyear/Spark-Pipeline-Framework/examples/ingestion/simple/file-ingestion.conf"))

    new JobLauncher(config).start()
  }
}
