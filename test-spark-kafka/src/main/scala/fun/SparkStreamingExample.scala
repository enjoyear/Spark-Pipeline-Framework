package fun

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Server: nc -l 3333
  * Client: nc localhost 3333
  */
object SparkStreamingExample extends App {
  val sparkConf = new SparkConf().setMaster("local[4]").setAppName("NetworkWordCount")
  val ssc = new StreamingContext(sparkConf, Seconds(2))

  // Create a DStream that will connect to localhost:9999
  // Establish a socket stream on top of D-Streams that reads line by line, every two seconds.
  val lines = ssc.socketTextStream("localhost", 3333)

  val words = lines.flatMap(_.split(" "))
  val pairs = words.map(word => (word, 1))
  val wordCounts = pairs.reduceByKey(_ + _)

  // Print first 10 lines
  wordCounts.print()

  ssc.start()
  ssc.awaitTermination()
}
