package com.bigdata.kafkaspark;

import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class StreamingConsumer {
    public static void main(String[] args) throws StreamingQueryException {
       // Hadoop binary adding
        System.setProperty("hadoop.home.dir", "C:\\hadoop-common-2.2.0-bin-master");
      //SparkSession creating
        SparkSession sparkSession = SparkSession.builder().appName("Spark Steam Chat Listener")
                .master("local").getOrCreate();

        //determine datatype when fetching data
        StructType mySchema = new StructType()
                .add("product",DataTypes.StringType,true)
                .add("time", DataTypes.TimestampType,true);

        //sparkSession reading data from kafka
        Dataset<Row> load = sparkSession.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("header",false)
                .option("subscribe", "search").load();

        //data that in load parsing to json format and converting data model
       Dataset<SearchProductModel> dataset = load.selectExpr("CAST (VALUE AS string) message")
                .select(functions.from_json(functions.col("message"), mySchema).as("json"))
               .select("json.*")
                .as(Encoders.bean(SearchProductModel.class));



        // filtering timestamp format
        Dataset<Row> countData = dataset.groupBy(functions.window(dataset.col("time"), "1 minute"), dataset.col("product")).count().sort(functions.desc("count"));

        // writing on console
        StreamingQuery start = countData.writeStream()
                .outputMode("complete")
                .format("console")
                .start();

        start.awaitTermination();


    }
}
