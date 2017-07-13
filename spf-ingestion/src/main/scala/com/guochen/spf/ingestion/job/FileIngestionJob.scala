package com.guochen.spf.ingestion.job

import com.guochen.spf.core.{SparkJob, SparkJobValid, SparkJobValidation}
import com.typesafe.config.Config
import org.apache.spark.SparkContext


class FileIngestionJob(conf: Config) extends SparkJob {
  override def runJob(sc: SparkContext, jobConfig: Config): Any = {
    println(conf.getString("Hi"))
  }

  override def validate(sc: SparkContext, config: Config): SparkJobValidation = SparkJobValid
}