package core

import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import utils.http.HttpUtils

/**
  * Created by muratcengiz on 02/07/17.
  */
object ReviewCollector {

  def retrieveReviews(js : JsObject, key : String) : JsObject = {
    val movieId = (js \ "id").as[Int]
    var reviews = List[JsValue]()
    val url = "https://api.themoviedb.org/3/movie/" + movieId + "/reviews?api_key=" + key + "&language=en-US&page="
    val json = HttpUtils.getContent(url + "1")
    json match {
      case Some(x) => {
        val pages = (x \ "total_pages").as[Int]
        for (page <- 1 until pages + 1) {
          reviews = reviews ::: retrieveResultsInPage(url, page)
        }
      }
    }
    js + ("reviews" -> Json.toJson(reviews))
  }

  def retrieveResultsInPage(url : String, page : Int) : List[JsValue] = {
    val json = HttpUtils.getContent(url + page)
    json match {
      case Some(x) => {
        (x \ "results").as[JsArray].value.toList
      }
    }
  }

}
