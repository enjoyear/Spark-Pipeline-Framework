package com.guochen.spf.ingestion.job

import com.guochen.spf.core.{SparkJob, SparkJobValid, SparkJobValidation}
import com.guochen.spf.ingestion.config.ConfigurationKeys
import com.typesafe.config.Config
import org.apache.spark.SparkContext


class FileIngestionJob(config: Config) extends SparkJob(config) {
  val source = config.getConfig(ConfigurationKeys.SOURCE_ROOT)
  val converter = config.getConfig(ConfigurationKeys.CONVERTER_ROOT)
  val checker = config.getConfig(ConfigurationKeys.CHECKER_ROOT)
  val publisher = config.getConfig(ConfigurationKeys.PUBLISHER_ROOT)

  override def validate(sc: SparkContext): SparkJobValidation = SparkJobValid

  override def run(sc: SparkContext): Any = {
    val filePath = source.getString(ConfigurationKeys.INPUT_FILE)
    val fileRdd = sc.textFile(filePath)
    fileRdd.foreach(println)
  }
}