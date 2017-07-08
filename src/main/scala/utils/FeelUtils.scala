package utils

import play.api.libs.json.{JsError, JsSuccess, Json}

/**
  * Created by muratcengiz on 08/07/17.
  */
object FeelUtils {

  case class Feels(
                  id: Int,
                  positivity: Float,
                  subjectivity: Float
                  )

  implicit val feelsFormat = Json.format[Feels]

  def StringToFeels(str : String) = Json.parse(str).validate[Feels] match {
    case JsError(e) => println(e); None
    case JsSuccess(t, _) => Some(t)
  }

}
