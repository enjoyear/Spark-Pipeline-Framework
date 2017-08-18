package fun

import java.util.Properties

import org.apache.spark.sql.SparkSession

object SparkTwitterStreaming extends App {
  private val props = new Properties
  private val configStream = getClass.getResourceAsStream("/twitter.conf")
  props.load(configStream)

  println(props)

  private val builder = SparkSession.builder().master("local").appName("twitter-streaming")
  private val session = builder.getOrCreate()

  // Set the system properties so that Twitter4j library used by twitter stream
  // can use them to generat OAuth credentials
  System.setProperty("twitter4j.oauth.consumerKey", props.getProperty("consumerKey"))
  System.setProperty("twitter4j.oauth.consumerSecret", props.getProperty("consumerSecret"))
  System.setProperty("twitter4j.oauth.accessToken", props.getProperty("accessToken"))
  System.setProperty("twitter4j.oauth.accessTokenSecret", props.getProperty("accessTokenSecret"))

  session
}
