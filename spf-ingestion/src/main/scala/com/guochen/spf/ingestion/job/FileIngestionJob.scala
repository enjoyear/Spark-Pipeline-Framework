package com.guochen.spf.ingestion.job

import com.guochen.spf.core.{SparkJob, SparkJobValid, SparkJobValidation}
import com.typesafe.config.Config
import org.apache.spark.SparkContext


class FileIngestionJob(config: Config) extends SparkJob(config) {
  override def validate(sc: SparkContext): SparkJobValidation = SparkJobValid

  override def run(sc: SparkContext): Any = {
    println(config.getString("Hi"))
  }
}