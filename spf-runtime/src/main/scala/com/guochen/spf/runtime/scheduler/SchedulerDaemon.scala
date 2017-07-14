package com.guochen.spf.runtime.scheduler

import java.io.File
import collection.JavaConverters._

import com.guochen.spf.core.{SparkJob, SparkJobInvalid, SparkJobValid}
import com.guochen.spf.runtime.config.ConfigurationKeys
import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

object SchedulerDaemon extends App {
  //  private val config = ConfigFactory.parseFile(new File(getClass.getResource("file-ingestion.conf").getFile))
  private val config = ConfigFactory.parseFile(new File("/Users/chguo/repos/enjoyear/Spark-Pipeline-Framework/examples/ingestion/simple/file-ingestion.conf"))

  start(args, setupSpark().sparkContext)
  //start(args, null)

  def setupSpark(): SparkSession = {
    val clusterConfig = config.getConfig(ConfigurationKeys.SPARK_CLUSTER)

    val sessionBuilder = SparkSession
      .builder()
      .master(clusterConfig.getString(ConfigurationKeys.KEY_SPARK_MASTER))
      .appName(clusterConfig.getString(ConfigurationKeys.KEY_SPARK_APP_NAME))

    val sparkConfig = config.getConfig(ConfigurationKeys.SPARK_CONFIG).entrySet()
    sparkConfig.asScala.foreach(entry => {
      sessionBuilder.config(entry.getKey, entry.getValue.toString)
    })
    sessionBuilder.getOrCreate()
  }

  def start(args: Array[String], sparkContext: SparkContext) {
    val jobConfig = config.getConfig(ConfigurationKeys.JOB_ROOT)
    val jobClassName = jobConfig.getString(ConfigurationKeys.KEY_JOB_CLASS)
    val jobClass = Class.forName(jobClassName)
    val ctor = jobClass.getDeclaredConstructor(Class.forName("com.typesafe.config.Config"))
    val job = ctor.newInstance(jobConfig.getConfig("config")).asInstanceOf[SparkJob]
    job.validate(sparkContext) match {
      case SparkJobValid => job.run(sparkContext)
      case SparkJobInvalid(msg) => println(msg)
    }
  }
}
