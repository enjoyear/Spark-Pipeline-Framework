package com.chen.guo

import java.io.File

import com.chen.guo.core.{SparkJob, SparkJobValid, SparkJobValidation}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.SparkContext


class FileIngestionJob extends SparkJob {
  override def runJob(sc: SparkContext, jobConfig: Config): Any = {
    val config: Config = ConfigFactory.parseFile(new File("/Users/chguo/repos/enjoyear/Spark-Pipeline-Framework/spf-ingestion/src/main/resources/com/chen/guo/simple.conf"))

    println(config.getString("Hi"))
  }

  override def validate(sc: SparkContext, config: Config): SparkJobValidation = SparkJobValid
}