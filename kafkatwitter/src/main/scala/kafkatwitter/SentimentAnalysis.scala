package kafkatwitter

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree
import edu.stanford.nlp.util.CoreMap

import scala.collection.JavaConverters._


object SentimentAnalysis {

  val props : Properties = new Properties()

  props.put("annotators", "tokenize, ssplit, parse, sentiment")

  val pipeline : StanfordCoreNLP = new StanfordCoreNLP(props)


  def getSentiment (sentiment: Int): String = sentiment match {
    case x if x == 0 || x ==1 => "Negative"
    case 2 => "Neutral"
    case x if x == 3 || x == 4 => "Positive"
  }

  def findSentiment (text : String) : List[(String,String)]  = {


    //We create a document from a text
    val document: Annotation = new Annotation(text)

    //Run the annotator
    pipeline.annotate(document)

    //We list the sentences
    val sentences : List[CoreMap] = document.get(classOf[CoreAnnotations.SentencesAnnotation]).asScala.toList

    //We check if the sentence is positive or negative
    sentences.map(sentence => (sentence, sentence.get(classOf[SentimentAnnotatedTree])))
      .map{case (sentence, tree) => (sentence.toString(), getSentiment(RNNCoreAnnotations.getPredictedClass(tree)))}
    //.foreach(println)



  }


}