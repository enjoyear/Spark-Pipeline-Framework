package com.guochen.spf.ingestion.job

import com.guochen.spf.core.{SparkJob, SparkJobValid, SparkJobValidation}
import com.guochen.spf.ingestion.config.ConfigurationKeys
import com.typesafe.config.Config
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}


class FileIngestionJob(config: Config) extends SparkJob(config) {
  private val source = config.getConfig(ConfigurationKeys.SOURCE_ROOT)
  private val converter = config.getConfig(ConfigurationKeys.CONVERTER_ROOT)
  private val checker = config.getConfig(ConfigurationKeys.CHECKER_ROOT)
  private val publisher = config.getConfig(ConfigurationKeys.PUBLISHER_ROOT)

  override def validate(ss: SparkSession): SparkJobValidation = SparkJobValid

  def getSchema(): StructType = StructType(
    Seq(
      StructField(name = "name", dataType = StringType, nullable = false),
      StructField(name = "age", dataType = IntegerType, nullable = false))
  )

  override def run(ss: SparkSession): Any = {
    val filePath = source.getString(ConfigurationKeys.SOURCE.LOCATION)
    val fileRdd = ss.sparkContext.textFile(filePath)
    val dataFrame = ss.read.csv(filePath)
    dataFrame.foreach(r => {

    })
    dataFrame.write
      .format(publisher.getString(ConfigurationKeys.PUBLISHER.FORMAT))
      .mode(publisher.getString(ConfigurationKeys.PUBLISHER.MODE))
      .save(publisher.getString(ConfigurationKeys.PUBLISHER.LOCATION))
  }
}