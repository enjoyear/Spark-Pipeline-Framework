package com.guochen.spf.runtime.scheduler

import java.io.File

import com.guochen.spf.runtime.JobLauncher
import com.typesafe.config.ConfigFactory

object SchedulerDaemon extends App {
  //  private val config = ConfigFactory.parseFile(new File(getClass.getResource("file-ingestion.conf").getFile))
  private val config = ConfigFactory.parseFile(new File("/Users/chguo/repos/enjoyear/Spark-Pipeline-Framework/examples/ingestion/simple/file-ingestion.conf"))

  new JobLauncher(config).start()
  //start(args, null)
}
