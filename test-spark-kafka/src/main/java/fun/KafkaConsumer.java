package fun;


import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example of creating a High Level Consumer
 * <p>
 * Configuration details can be found at http://kafka.apache.org/08/documentation/#configuration
 */
public class KafkaConsumer {
  public static void main(String[] args) throws Exception {
    Properties props = new Properties();
    //Kafka uses ZooKeeper to store offsets of messages consumed for a specific topic and partition by this Consumer Group
    props.put("zookeeper.connect", "localhost:2181");
    props.put("group.id", "my-test-group");
    props.put("zookeeper.session.timeout.ms", "400");
    //zookeeper.sync.time is milliseconds a ZooKeeper ‘follower’ can be behind the master before an error occurs
    props.put("zookeeper.sync.time.ms", "200");
    //how often updates to the consumed offsets are written to ZooKeeper. Note that since the commit frequency is time based instead of # of messages consumed, if an error occurs between updates to ZooKeeper on restart you will get replayed messages.
    props.put("auto.commit.interval.ms", "1000");
    ConsumerConfig consumerConfig = new ConsumerConfig(props);

    ConsumerConnector consumer = Consumer.createJavaConsumerConnector(consumerConfig);
    Map<String, Integer> topicCountMap = new HashMap<>();
    int threadCount = 2;
    topicCountMap.put(KafkaTwitterProducer.TOPIC_NAME, threadCount); //2 threads.

    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
    List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(KafkaTwitterProducer.TOPIC_NAME);

    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    int threadNumber = 0;
    for (final KafkaStream<byte[], byte[]> stream : streams) {
      executor.submit(new MyConsumer(stream, threadNumber++));
    }
    //consumer.shutdown();
  }
}

class MyConsumer implements Runnable {
  private KafkaStream<byte[], byte[]> stream;
  private int threadNumber;

  MyConsumer(KafkaStream<byte[], byte[]> stream, int threadNumber) {
    this.stream = stream;
    this.threadNumber = threadNumber;
  }

  public void run() {
    for (MessageAndMetadata<byte[], byte[]> aStream : stream)
      System.out.println("Thread " + threadNumber + ": " + new String(aStream.message()));
    System.out.println("Shutting down Thread: " + threadNumber);
  }
}
