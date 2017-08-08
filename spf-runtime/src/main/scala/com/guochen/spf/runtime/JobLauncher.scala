package com.guochen.spf.runtime

import com.guochen.spf.core.{SparkJob, SparkJobInvalid, SparkJobValid}
import com.guochen.spf.runtime.config.ConfigurationKeys
import com.typesafe.config.Config
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConverters._


class JobLauncher(config: Config) {

  def setupSpark(): SparkSession = {
    val clusterConfig = config.getConfig(ConfigurationKeys.SPARK_CLUSTER)

    val sessionBuilder = SparkSession
      .builder()
      .master(clusterConfig.getString(ConfigurationKeys.KEY_SPARK_MASTER))
      .appName(clusterConfig.getString(ConfigurationKeys.KEY_SPARK_APP_NAME))

    if (clusterConfig.getBoolean(ConfigurationKeys.ENABLE_HIVE_SUPPORT))
      sessionBuilder.enableHiveSupport()

    val sparkConfig = config.getConfig(ConfigurationKeys.SPARK_CONFIG).entrySet()
    sparkConfig.asScala.foreach(entry => {
      sessionBuilder.config(entry.getKey, entry.getValue.toString)
    })
    sessionBuilder.getOrCreate()
  }

  def start() {
    val sparkContext: SparkSession = setupSpark()
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
