package core

import java.io.{File, PrintWriter}

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import play.api.libs.json.{JsArray, JsObject, JsValue}
import utils.MovieUtils
import utils.MovieUtils.Movie
import utils.http.HttpUtils

object MovieCollector {

  var apiKey = ""
  var collectedMovies = 0
  var collectedReviews = 0

  val moviesToFind = List("Wonder Woman", "Transformers", "300", "Batman")
  val pathToFile = "data/movie.txt"

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

  def retrieveMovies(key : String) = {
    apiKey = key
    val writer = new PrintWriter(new File(pathToFile))
    //getMoviesFromFile(writer)
    getMostPopularMovies(writer)
    writer.close()
    println(collectedReviews + " reviews collected")
  }

  /**
    * Ajoute les films dans moviesToFind dans la BD.
    * C'est lent, en faire pour quelques films mais pas la majorité
    * @param writer : Fichier à écrire.
    */
  def getMoviesFromFile(writer : PrintWriter) = {
    moviesToFind.foreach(query => {
      writeMovieToFile(query, writer)
      println(query)
    })
  }

  /**
    * Ecrit les films les plus populaires dans un fichier (+ les commentaires des films)
    * @param writer : fichier à écrire
    */
  def getMostPopularMovies(writer : PrintWriter) = {
    val numberTotalPages = 10 // nombre de pages à visiter (20 films par page)
    val url = "https://api.themoviedb.org/3/movie/popular?api_key=" + apiKey + "&language=en-US&page="
    for (pageNumber <- 1 to numberTotalPages) {
      val results = retrieveResultsInPage(url, pageNumber)
      results.foreach(jsValue => {
        getMovieDetailsWithReviews((jsValue \ "id").as[Int], writer)
      })
    }
  }

  /**
    * Fonction utilitaire.
    * Trouve la liste des commentaires à partir d'un ID de film
    * @param movieId : ID du film
    * @param writer : fichier à écrire
    */
  def getMovieDetailsWithReviews(movieId : Int, writer : PrintWriter) = {
    val url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey
    HttpUtils.getContent(url) match {
      case Some(json) => {
        val newJson = ReviewCollector.retrieveReviews(json.as[JsObject], apiKey)
        val numberReviews = (newJson \ "reviews").as[JsArray].value.size
        collectedMovies += 1
        collectedReviews += numberReviews
        println(collectedMovies + " : " + (newJson \ "original_title").as[String] + " (" + numberReviews + " reviews)")
        writer.println(newJson.toString)
      }
    }
  }

  /**
    * Fonction utilitaire.
    * Permet de faire une requête et de retourner le champ "results" sous forme de List
    * @param url : la requête
    * @param page : le numéro de page à chercher
    */
  def retrieveResultsInPage(url : String, page : Int) : List[JsValue] = {
    val json = HttpUtils.getContent(url + page)
    json match {
      case Some(x) => {
        (x \ "results").as[JsArray].value.toList
      }
    }
  }

  /**
    * Recherche de films en live, sans passer par un fichier.
    * (Gourmand. Utile que pour se la péter devant le prof en lui demandant quel film il veut qu'on
    * ajoute à la BD)
    * @return : Liste des films proposées (limitée à la page 1)
    */
  def findMovieByName(name : String) : List[Movie] = {
    val url = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&query=" + name.replaceAll("\\s+", "+") + "&language=en-US&page=1&include_adult=false"
    HttpUtils.getContent(url) match {
      case None => List()
      case Some(json) => (json \ "results").as[JsArray].value.flatMap(jsvalue => MovieUtils.StringToMovie(jsvalue.toString)).toList
    }
  }

  /**
    * Cherche un film et retourne l'ID de film du premier résultat proposé
    * @param name : Nom du film
    */
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

  /**
    * Devenu useless. Trop gourmand
    * Cherche un film avec un titre et écris dans un fichier avec les commentaires associés
    * @param name : Nom du film à chercher
    * @param writer : Fichier à écrire
    */
  def writeMovieToFile(name : String, writer : PrintWriter) = {
    val movieId = getFirstResultMovieId(name)
    movieId match {
      case Some(id) => {
        getMovieDetailsWithReviews(id, writer)
      }
    }
  }

}