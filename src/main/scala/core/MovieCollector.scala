package core

import java.io.{File, PrintWriter}

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import play.api.libs.json.{JsArray, JsObject}
import utils.MovieUtils
import utils.MovieUtils.Movie
import utils.http.HttpUtils

object MovieCollector {

  var apiKey = ""

  val moviesToFind = List("Wonder Woman", "Transformers")
  val pathToFile = "data/movies.txt"

  def retrieveMovies(key : String) = {
    apiKey = key
    val writer = new PrintWriter(new File(pathToFile))
    moviesToFind.foreach(query => {
      writeMovieToFile(query, writer)
    })
    writer.close()
  }

  /**
    *  Load the data from the json file and return an RDD of Movie
    */
  def loadMovies(): RDD[Movie] = {
    // Create the spark configuration and spark context
    val conf = new SparkConf()
      .setAppName("Spark Movies")
      .setMaster("local[*]")

    val sc = SparkContext.getOrCreate(conf)

    // Load the data and parse it into a Movie.
    sc.textFile(pathToFile)
      .flatMap(MovieUtils.StringToMovie)
  }

  def getFirstResultMovieId(name : String) : Option[Int] = {
    val url = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&query=" + name.replaceAll("\\s+", "+") + "&language=en-US&page=1&include_adult=false"
    HttpUtils.getContent(url) match {
      case Some(json) => {
        val results = (json \ "results").as[JsArray].value
        results.size match {
          case 0 => None
          case _ => Some((results.head \ "id").as[Int])
        }
      }
      case None => None
    }
  }

  def writeMovieToFile(name : String, writer : PrintWriter) = {
    val movieId = getFirstResultMovieId(name)
    movieId match {
      case Some(id) => {
        val url = "https://api.themoviedb.org/3/movie/" + id + "?api_key=" + apiKey
        HttpUtils.getContent(url) match {
          case Some(json) => {
            val newJson = ReviewCollector.retrieveReviews(json.as[JsObject], apiKey)
            writer.println(newJson.toString)
          }
        }
      }
    }
  }

  /**
    * Direct search, without passing through file
    */
  def findMovieByName(name : String) : List[Movie] = {
    val url = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&query=" + name.replaceAll("\\s+", "+") + "&language=en-US&page=1&include_adult=false"
    HttpUtils.getContent(url) match {
      case None => List()
      case Some(json) => (json \ "results").as[JsArray].value.flatMap(jsvalue => MovieUtils.StringToMovie(jsvalue.toString)).toList
    }
  }

}