package utils

import play.api.libs.json._
import utils.FeelUtils.Feels

object MovieUtils {

	case class Movie(
										id: Option[Int],
										title: Option[String],
										rating: Option[Double],
										release: Option[Int],
										author: Option[String],
										actors: Option[List[String]],
										genres: Option[List[String]],
										country: Option[String],
										reviews: Option[List[String]],
									  feels: Option[Feels]
									)

	implicit val movieFormat = Json.format[Movie]

	def StringToMovie(str : String) = Json.parse(str).validate[Movie] match {
		case JsError(e) => println(e); None
		case JsSuccess(t, _) => Some(t)
	}
}