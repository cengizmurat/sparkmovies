import core.MovieCollector

/**
  * Created by muratcengiz on 02/07/17.
  */
object Main {

  def main(args : Array[String]) : Unit = {
    val apiKey = args(0)
    MovieCollector.retrieveMovies(apiKey)
  }

}
