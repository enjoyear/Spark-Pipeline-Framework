package com.guochen.spf.runtime.scheduler

import java.io.File

import com.guochen.spf.ingestion.job.FileIngestionJob
import com.typesafe.config.{Config, ConfigFactory}

object SchedulerDaemon extends App {
  val configPath = getClass.getResource("file-ingestion.conf").getFile
  val config: Config = ConfigFactory.parseFile(new File(configPath))

  private val jobConfig = config.getConfig("SPF.spark.job")
  println(jobConfig.getString("class"))


  new FileIngestionJob(jobConfig.getConfig("conf")).runJob(null, null)
}
