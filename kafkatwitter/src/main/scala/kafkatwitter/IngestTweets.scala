package kafkatwitter

import org.json.JSONObject
import scala.util.Try
import sys.addShutdownHook
import org.apache.kafka.clients.producer.ProducerRecord

object IngestTweets extends App with Logging{
  
  var closing = false
  
  addShutdownHook {
    closing = true
    producer.close
    source.hosebirdClient.stop
  }
  
  log.info(Settings.config.toString)
  val source = Settings.tweetSource
  val producer = Settings.kafkaProducer
  val topic_raw = Settings.rawTopic
  val topic_sentiment = Settings.sentimentTopic
  
  while (!(source.hosebirdClient.isDone) & !(closing)) {
    Try{
    source.take() match {
      case Some(json) =>
        val jsnobject = new JSONObject(json);
        val text = jsnobject.get("text")
        log.info(s"${text}")
        val sentiment = SentimentAnalysis.findSentiment(text.toString())
        send(json,topic_raw)
        //sentiment.foreach(println)
        sentiment.foreach{case (msg,sentiment) => send(msg + "," + sentiment, topic_sentiment)}
      case None => 
        
    }
    }.getOrElse(None)
  }
  
  def send(msg:String, topicToSend: String): Unit = {
    val key =  TweetKey(Settings.filterTerms)
    val keyPayload = Json.ByteArray.encode(key)
    val payload = msg.map(_.toByte).toArray
    val record = new ProducerRecord[Array[Byte],Array[Byte]](topicToSend, keyPayload, payload)
    log.info(s"Sending to Kafka ${record}")
    producer.send(record)
  }
}