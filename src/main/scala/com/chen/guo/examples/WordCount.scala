package com.chen.guo.examples

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object WordCount extends App {
  private val exampleFilePath = getClass.getResource("/com/chen/guo/examples/WordCountExampleFile.txt").getPath

  private val ss = SparkSession
    .builder()
    .master("local")
    .appName("WordCountExp")
    .getOrCreate()
  private val rows = ss.sparkContext.textFile(exampleFilePath)
  private val wordCount: RDD[(String, Int)] = rows.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)

  import ss.implicits._

  wordCount.toDF("Word", "Count").show()
}
