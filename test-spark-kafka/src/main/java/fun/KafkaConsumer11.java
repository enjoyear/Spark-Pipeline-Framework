package fun;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example: https://stackoverflow.com/questions/34405124/kafka-0-9-0-new-java-consumer-api-fetching-duplicate-records?rq=1
 * <p>
 * Doc: https://kafka.apache.org/0110/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html
 */
public class KafkaConsumer11 {
  public static void main(String[] args) {
    ExecutorService executor = Executors.newFixedThreadPool(3);
    executor.submit(new ConsumerThread("test-topic", "myThread-1"));
    executor.submit(new ConsumerThread("test-topic", "myThread-2"));
  }

  static class ConsumerThread implements Runnable {
    private final KafkaConsumer<String, String> consumer;

    ConsumerThread(String topic, String name) {
      Thread.currentThread().setName(name);
      Properties props = new Properties();
      props.put("bootstrap.servers", "localhost:9092");
      props.put("group.id", "consumer-group-1");
      //Automatic Offset Committing
      props.put("enable.auto.commit", "true");
      props.put("auto.commit.interval.ms", "1000");
      //The deserializer settings specify how to turn bytes into objects
      props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      //If the consumer crashes or is unable to send heartbeats for a duration of session.timeout.ms, then the consumer will be considered dead and its partitions will be reassigned.
      props.put("session.timeout.ms", "30000");

      consumer = new KafkaConsumer<>(props);
      consumer.subscribe(Collections.singletonList(topic));
    }

    public void run() {
      try {
        while (true) {
          ConsumerRecords<String, String> records = consumer.poll(10000L);
          System.out.println("Partition Assignment to this Consumer: " + consumer.assignment());
          for (ConsumerRecord<String, String> record : records) {
            System.out.printf("Received @thread '%s': offset=%d, <%s, %s>%n",
                Thread.currentThread().getName(), record.offset(), record.key(), record.value());
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        //The consumer maintains TCP connections to the necessary brokers to fetch data. Failure to close the consumer after use will leak these connections.
        consumer.close();
      }
    }
  }
}
