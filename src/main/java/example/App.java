package example;

import com.google.protobuf.AbstractMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class App implements Runnable {
    private final KafkaConsumer<String, String> consumer;
    private final List<TopicPartition> topics;
    private final int id;

    public String server;

    public App(String server, int id,
               String groupId,
               List<TopicPartition> topics) {

        this.server = server;
        this.id = id;
        this.topics = topics;
        Properties props = new Properties();
        props.put("bootstrap.servers", server);
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {

            consumer.assign(this.topics);
            consumer.seekToBeginning(this.topics);

            int messageCount = 0;
            while (true) {

                ConsumerRecords<String, String> records = consumer.poll(5000);
                for (ConsumerRecord<String, String> record : records) {
                    AbstractMessage abstractMessage = new ProtobufMessageUtil().decode(record.value());

                    System.out.printf("\n\nFound protobuf message: %s", abstractMessage.getClass().getSimpleName());
                    System.out.println("\n\t" + ProtobufToJSON.toJSONObjectModel(abstractMessage));
                    messageCount++;
                    if (messageCount > 3) {
                        consumer.close();
                        System.exit(0);
                    }
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }

    public static void main(String args[]) {
        if(args.length < 2) {
            System.out.println("First argument should be the Kafka server string: kafkalhost:9092");
            System.out.println("Second argument should be Kafka topic: ZIFTEN.DATACOLLECTION_");
        }

        Logger.getRootLogger().setLevel(Level.OFF);
        String server = args[0];
        String topic = args[1];
        System.out.printf("Connecting to kafka server at %s with topic %s \n", server, topic);
        new App(server, 0, "MY-CONSUMER", Arrays.asList(new TopicPartition(topic, 0))).run();

    }
}