package com.guochen.spf.ingestion.example

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object WordCount extends App {
  private val ss = SparkSession
    .builder()
    .master("local")
    .appName("WordCountExp")
    .getOrCreate()

  /*
    1. Spark configuration will use only keys in the spark namespace
    2. Alternatively, we can pass arguments as application arguments as in
       ./bin/spark-submit \
       --class <main-class> \
       --master <master-url> \
       --deploy-mode <deploy-mode> \
       --conf <key>=<value> \
       ... # options
       <application-jar> \
       [application-arguments] <--- APP ARGUMENTS HERE!
   */
  private val exampleFilePath = ss.sparkContext.getConf.get(
    "spark.chen.guo.word.count.filepath",
    getClass.getResource("/com/chen/guo/example/WordCountExampleFile.txt").getPath)

  private val rows = ss.sparkContext.textFile(exampleFilePath)
  private val wordCount: RDD[(String, Int)] = rows.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)

  import ss.implicits._

  wordCount.toDF("Word", "Count").show()
}
