package fun

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Spark Streaming Programming Guide
  * https://spark.apache.org/docs/latest/streaming-programming-guide.html#overview
  */
object SparkTwitterStreaming extends App {
  private val props = new Properties
  private val configStream = getClass.getResourceAsStream("/twitter.conf")
  props.load(configStream)
  println(props)

  // Set the system properties for twitter stream
  System.setProperty("twitter4j.oauth.consumerKey", props.getProperty("consumerKey"))
  System.setProperty("twitter4j.oauth.consumerSecret", props.getProperty("consumerSecret"))
  System.setProperty("twitter4j.oauth.accessToken", props.getProperty("accessToken"))
  System.setProperty("twitter4j.oauth.accessTokenSecret", props.getProperty("accessTokenSecret"))

  // Make sure to set master to local mode with at least 2 threads
  // 1 thread is used for collecting the incoming streams (receiver thread)
  // another thread for processing input stream
  val sparkConf = new SparkConf().setMaster("local[4]").setAppName("TwitterStream")
  // Set the Spark StreamingContext to create a DStream for every 10 seconds
  val ssc = new StreamingContext(sparkConf, Seconds(10))

  val stream = TwitterUtils.createStream(ssc, None, Seq("iPhone"))
  val hashTags = stream.flatMap(status => status.getText.split(" ").filter(_.startsWith("#")))

  // Get the top hashtags over the previous 60 sec window
  // The default slideDuration is the StreamContext's batch duration
  val topCounts60 = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(60))
    .map { case (topic, count) => (count, topic) }
    .transform(_.sortByKey(ascending = false))

  // Get the top hashtags over the previous 10 sec window
  val topCounts10 = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(10))
    .map { case (topic, count) => (count, topic) }
    .transform(_.sortByKey(ascending = false))

  stream.print()
  topCounts60.foreachRDD(rdd => {
    val topList = rdd.take(10)
    println("\nPopular topics in last 60 seconds (%s total):".format(rdd.count()))
    topList.foreach { case (count, tag) => println("%s (%s tweets)".format(tag, count)) }
  })
  topCounts10.foreachRDD(rdd => {
    val topList = rdd.take(10)
    println("\nPopular topics in last 10 seconds (%s total):".format(rdd.count()))
    topList.foreach { case (count, tag) => println("%s (%s tweets)".format(tag, count)) }
  })

  ssc.start()
  ssc.awaitTermination()
}
