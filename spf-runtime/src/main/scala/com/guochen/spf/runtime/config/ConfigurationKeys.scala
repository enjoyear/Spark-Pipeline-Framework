package com.guochen.spf.runtime.config

object ConfigurationKeys {
  val ROOT = "spf"

  //CLUSTER SPECIFIC
  val SPARK_CLUSTER = s"$ROOT.spark.cluster"
  val KEY_SPARK_MASTER = "master"
  val KEY_SPARK_APP_NAME = "app_name"

  //SPARK SPECIFIC
  val SPARK_CONFIG = s"$ROOT.spark.config"

  //JOB SPECIFIC
  val JOB_ROOT = s"$ROOT.job"
  val KEY_JOB_CLASS = "class"
}
