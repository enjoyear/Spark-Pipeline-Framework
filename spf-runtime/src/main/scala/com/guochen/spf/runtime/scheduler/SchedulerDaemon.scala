package com.guochen.spf.runtime.scheduler

import java.io.File

import com.guochen.spf.core.{SparkJob, SparkJobInvalid, SparkJobValid}
import com.typesafe.config.{Config, ConfigFactory}

object SchedulerDaemon extends App {
  start(args)

  def start(args: Array[String]) {
    val configPath = getClass.getResource("file-ingestion.conf").getFile
    val config: Config = ConfigFactory.parseFile(new File(configPath))

    val jobConfig = config.getConfig("SPF.spark.job")
    val jobClassName = jobConfig.getString("class")
    val jobClass = Class.forName(jobClassName)
    val ctor = jobClass.getDeclaredConstructor(Class.forName("com.typesafe.config.Config"))
    val job = ctor.newInstance(jobConfig.getConfig("config")).asInstanceOf[SparkJob]
    job.validate(null) match {
      case SparkJobValid => job.run(null)
      case SparkJobInvalid(msg) => println(msg)
    }
  }
}
