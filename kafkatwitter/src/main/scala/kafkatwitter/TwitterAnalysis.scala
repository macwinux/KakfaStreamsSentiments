package com.universia.twittergen

import java.util.Properties

import com.universia.twittergen.kafkatwitter.SentimentAnalysis
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.json.JSONObject

object TwitterAnalysis extends Logging {

  def main(args: Array[String]): Unit = {

    val config: Properties = new Properties

    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "twittergen")
    config.put(ConsumerConfig.CLIENT_ID_CONFIG, "twittergen")
    //config.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString)
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Settings.brokers)
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass)
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)
    //config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0")

    val builder: KStreamBuilder = new KStreamBuilder

    val tweets: KStream[String, String] = builder.stream[String, String](Settings.rawTopic)

    val text: KStream[String, String] = tweets.mapValues {
      value => new JSONObject(value).get("text").toString
    }

    val sentiment: KStream[String, Seq[(String, String)]] = text.mapValues[Seq[(String, String)]](
      value => SentimentAnalysis.findSentiment(value))

    val textSent: KStream[String, String] = sentiment.mapValues[String]{ value: Seq[(String,String)] =>
           value.foldLeft ("")((a,b)=>  a + "\n(" +  b._1 + "; " + b._2  + ")")

    }


    textSent.to(Settings.sentimentTopic)


    /*textSent.foreach {
    case (key, value) => log.info(s"Sentiment send to topic ${value}")
    }*/

    val streams : KafkaStreams = new KafkaStreams(builder, config)

    streams.cleanUp()
    streams.start()

    System.out.println(streams.toString)

    Runtime.getRuntime.addShutdownHook(new Thread{
      override def run(): Unit = {
        streams.close()
      }
    })

  }
}
