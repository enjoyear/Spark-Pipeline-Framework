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
 * Example of a High Level Consumer
 * <p>
 * How to:
 * 1. Start zookeeper locally: bin/zookeeper-server-start.sh config/zookeeper.properties
 * Make sure port number is 2181
 * 2. Create a kafka topic: bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test-topic
 * 3. Start a kafka producer: bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic
 */
public class KafkaConsumer08 {
  public static void main(String[] args) throws Exception {
    Properties kafkaConf = getProperties();

    ConsumerConfig consumerConfig = new ConsumerConfig(kafkaConf);

    ConsumerConnector consumer = Consumer.createJavaConsumerConnector(consumerConfig);
    Map<String, Integer> topicCountMap = new HashMap<>();
    int threadCount = 2;
    topicCountMap.put(KafkaTwitterProducer.TOPIC_NAME, threadCount); //2 threads.

    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
    List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(KafkaTwitterProducer.TOPIC_NAME);

    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    int threadNumber = 0;
    for (final KafkaStream<byte[], byte[]> stream : streams) {
      executor.submit(new Consumer08Runnable(stream, threadNumber++));
    }
    //consumer.shutdown();
  }

  /**
   * Configuration ref: http://kafka.apache.org/08/documentation/#configuration
   */
  private static Properties getProperties() {
    Properties props = new Properties();
    //Kafka uses ZooKeeper to store offsets of messages consumed for a specific topic and partition by this Consumer Group
    props.put("zookeeper.connect", "localhost:2181");
    props.put("group.id", "my-test-group");
    props.put("zookeeper.session.timeout.ms", "400");
    //zookeeper.sync.time is milliseconds a ZooKeeper ‘follower’ can be behind the master before an error occurs
    props.put("zookeeper.sync.time.ms", "200");
    //how often updates to the consumed offsets are written to ZooKeeper. Note that since the commit frequency is time based instead of # of messages consumed, if an error occurs between updates to ZooKeeper on restart you will get replayed messages.
    props.put("auto.commit.interval.ms", "1000");
    return props;
  }

  static class Consumer08Runnable implements Runnable {
    private KafkaStream<byte[], byte[]> stream;
    private int threadNumber;

    Consumer08Runnable(KafkaStream<byte[], byte[]> stream, int threadNumber) {
      this.stream = stream;
      this.threadNumber = threadNumber;
    }

    public void run() {
      for (MessageAndMetadata<byte[], byte[]> aStream : stream)
        System.out.println("Thread " + threadNumber + ": " + new String(aStream.message()));
      System.out.println("Shutting down Thread: " + threadNumber);
    }
  }
}
