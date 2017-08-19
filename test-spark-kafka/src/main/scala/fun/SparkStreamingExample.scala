package fun

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * To run this on your local machine, you need to first run a Netcat server
  * Server: nc -lk 9999
  */
object SparkStreamingExample extends App {
  val sparkConf = new SparkConf().setMaster("local[4]").setAppName("NetworkWordCount")
  val ssc = new StreamingContext(sparkConf, Seconds(10))

  // Create a DStream that will connect to localhost:9999
  // Establish a socket stream on top of D-Streams that reads line by line, every 10 seconds.
  val lines = ssc.socketTextStream("localhost", 9999)

  val words = lines.flatMap(_.split(" "))
  val pairs = words.map(word => (word, 1))
  val wordCounts = pairs.reduceByKey(_ + _)

  // Print first 10 lines
  wordCounts.print()

  ssc.start()
  ssc.awaitTermination()
}
