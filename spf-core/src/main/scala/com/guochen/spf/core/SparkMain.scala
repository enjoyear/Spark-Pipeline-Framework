package com.guochen.spf.core

import org.apache.spark.sql.SparkSession


object SparkMain extends App {
  val spark: SparkSession =
    SparkSession
      .builder()
      .appName("AppName")
      .config("spark.master", "local")
      .getOrCreate()

  val data = Array(1, 2, 3, 4, 5)
  val rdd = spark.sparkContext.parallelize(data)
  rdd.foreach(println)
}
