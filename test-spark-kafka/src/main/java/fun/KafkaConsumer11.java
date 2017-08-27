package fun;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Collections;
import java.util.List;
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
      //Instead of relying on the consumer to periodically commit consumed offsets, users can also control when records should be considered as consumed and hence commit their offsets. This is useful when the consumption of the messages is coupled with some processing logic and hence a message should not be considered as consumed until it is completed processing.
      props.put("enable.auto.commit", "true");
      props.put("auto.commit.interval.ms", "1000");
      //The deserializer settings specify how to turn bytes into objects
      props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      //If the consumer crashes or is unable to send heartbeats for a duration of session.timeout.ms, then the consumer will be considered dead and its partitions will be reassigned.
      props.put("session.timeout.ms", "30000");

      consumer = new KafkaConsumer<>(props);

      //Instead of subscribing to the topic using subscribe, you just call assign(Collection) with the full list of partitions that you want to consume for Manual Partition Assignment
      //Manual partition assignment does NOT use group coordination, so consumer failures will not cause assigned partitions to be rebalanced. Each consumer acts INDEPENDENTLY EVEN IF it shares a groupId with another consumer. To avoid offset commit conflicts, you should usually ENSURE that the GROUPID IS UNIQUE for EACH consumer instance.
      //Note that!!! it isn't possible to mix manual partition assignment (i.e. using assign) with dynamic partition assignment through topic subscription (i.e. using subscribe).
      consumer.subscribe(Collections.singletonList(topic));
    }

    public void run() {
      try {
        while (true) {
          ConsumerRecords<String, String> records = consumer.poll(10000L);
          System.out.println("Partition Assignment to this Consumer: " + consumer.assignment());
          for (TopicPartition partition : records.partitions()) {
            List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
            //We can do something by partition
          }

          for (ConsumerRecord<String, String> record : records) {
            System.out.printf("Thread=%s, partition=%d: offset=%d, <%s, %s>%n",
                Thread.currentThread().getName(),
                record.partition(), record.offset(),
                record.key(), record.value());
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
