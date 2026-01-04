package se.liu.ida.tdp024.account.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

public class KafkaLogProducer {
    public static Producer<String, String> producer;

    public static Producer<String, String> createProducer() {
        if (producer == null) {
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:8090");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

            producer = new KafkaProducer<String, String>(props);
        }
        return producer;
    }

    public static void sendMessage(String topic, String message) {
        Producer<String, String> producer = createProducer();
        producer.send(new ProducerRecord<>(topic, message));
    }
}
