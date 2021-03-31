package com.bigdata.kafkaspark;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

public class MyProducer {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String topicName = "search";

        Gson gson = new Gson();
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        Producer producer = new KafkaProducer<String,String>(properties);

        while (true){
            System.out.println("Search: ");
            String inputData = input.nextLine();

            SearchProductModel searchProductModel = new SearchProductModel();
            searchProductModel.setProduct(inputData);

            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            searchProductModel.setTime(time);

            String jsonFormatData
                    = gson.toJson(searchProductModel);

            System.out.println(jsonFormatData);
            ProducerRecord <String,String> record = new ProducerRecord<String,String>(topicName,jsonFormatData);
            producer.send(record);
            System.out.println("Successfully, Data send to Kafka!");
        }

    }
}
