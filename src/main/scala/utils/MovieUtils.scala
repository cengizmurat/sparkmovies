package utils

import play.api.libs.json._
import utils.GenreUtils.Genre
import utils.ReviewUtils.Review

object MovieUtils {

	case class Movie(
										id: Option[Int],
										adult: Option[Boolean],
										genres: Option[List[Genre]],
										original_title: Option[String],
										//release_date: Date,
										popularity: Option[Double],
										vote_average: Option[Double],
										vote_count: Option[Int],
										reviews: Option[List[Review]]
									)

	implicit val movieFormat = Json.format[Movie]

	def StringToMovie(str : String) = Json.parse(str).validate[Movie] match {
		case JsError(e) => println(e); None
		case JsSuccess(t, _) => Some(t)
	}
}