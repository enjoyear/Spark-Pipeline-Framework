package fun;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * http://stdatalabs.blogspot.in/2016/09/spark-streaming-part-3-real-time.html
 * <p>
 * This app analyses the #hashtags in the tweets over the last 10 sec and 60 sec windows while users tweet about certain keywords.
 * <p>
 * <p>
 * How to get API Keys and Tokens for Twitter
 * http://www.slickremix.com/docs/how-to-get-api-keys-and-tokens-for-twitter/
 * <p>
 * Create App: https://apps.twitter.com/app/new
 * Access App: https://apps.twitter.com/
 */
public class KafkaTwitterProducer {
  public static void main(String[] args) throws Exception {
    Properties credentialProps = new Properties();
    InputStream configStream = KafkaTwitterProducer.class.getResourceAsStream("/twitter.conf");
    credentialProps.load(configStream);
    System.out.println(credentialProps);

    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setDebugEnabled(true)
        .setOAuthConsumerKey(credentialProps.getProperty("consumerKey"))
        .setOAuthConsumerSecret(credentialProps.getProperty("consumerSecret"))
        .setOAuthAccessToken(credentialProps.getProperty("accessToken"))
        .setOAuthAccessTokenSecret(credentialProps.getProperty("accessTokenSecret"));

    final LinkedBlockingQueue<Status> queue = new LinkedBlockingQueue<>(1000);
    TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
    twitterStream.addListener(createStatusListener(queue));

    FilterQuery query = new FilterQuery().track("iPhone", "Apple");
    twitterStream.filter(query);

    // Add Kafka producer config settings
    Properties props = new Properties();
    props.put("metadata.broker.list", "localhost:9092");
    props.put("bootstrap.servers", "localhost:9092");
    props.put("acks", "all");
    props.put("retries", 0);
    props.put("batch.size", 16384);
    props.put("linger.ms", 1);
    props.put("buffer.memory", 33554432);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    Producer<String, String> producer = new KafkaProducer<>(props);
    int j = 0;

    // poll for new tweets in the queue. If new tweets are added, send them
    // to the topic
    while (true) {
      Status ret = queue.poll();

      if (ret == null) {
        Thread.sleep(100);
        // i++;
      } else {
        for (HashtagEntity hashtage : ret.getHashtagEntities()) {
          System.out.println("Tweet:" + ret);
          System.out.println("Hashtag: " + hashtage.getText());
          producer.send(new ProducerRecord<>("test-topic", Integer.toString(j++), ret.getText()));
        }
      }
    }

  }

  private static StatusListener createStatusListener(LinkedBlockingQueue<Status> queue) {
    return new StatusListener() {
      @Override
      public void onStatus(Status status) {
        queue.offer(status);
      }

      @Override
      public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
      }

      @Override
      public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
      }

      @Override
      public void onScrubGeo(long userId, long upToStatusId) {
        System.out.println("Got scrub_geo event userId:" + userId + "upToStatusId:" + upToStatusId);
      }

      @Override
      public void onStallWarning(StallWarning warning) {
        System.out.println("Got stall warning:" + warning);
      }

      @Override
      public void onException(Exception ex) {
        ex.printStackTrace();
      }
    };
  }
}
