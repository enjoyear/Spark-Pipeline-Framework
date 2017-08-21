package fun;



import kafka.consumer.ConsumerConfig;

import java.util.Properties;

/**
 * Example of creating a High Level Consumer
 *
 * Configuration details can be found at http://kafka.apache.org/08/documentation/#configuration
 *
 */
public class KafkaConsumer {
  public static void main(String[] args) throws Exception {
    Properties props = new Properties();
    //Kafka uses ZooKeeper to store offsets of messages consumed for a specific topic and partition by this Consumer Group
    props.put("zookeeper.connect", "localhost:9092");
    props.put("group.id", "my-test-group");
    props.put("zookeeper.session.timeout.ms", "400");
    //zookeeper.sync.time is milliseconds a ZooKeeper ‘follower’ can be behind the master before an error occurs
    props.put("zookeeper.sync.time.ms", "200");
    //how often updates to the consumed offsets are written to ZooKeeper. Note that since the commit frequency is time based instead of # of messages consumed, if an error occurs between updates to ZooKeeper on restart you will get replayed messages.
    props.put("auto.commit.interval.ms", "1000");
    ConsumerConfig consumerConfig = new ConsumerConfig(props);

  }
}
