package utils

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}

/**
  * Created by muratcengiz on 02/07/17.
  */
object ReviewUtils {

  case class Review(
                     id: Option[String],
                     author: Option[String],
                     content: Option[String],
                     url: Option[String]
                   )

  implicit val reviewFormat = Json.format[Review]

  def StringToReview(str: String) = Json.parse(str).validate[Review] match {
    case JsError(e) => println(e); None
    case JsSuccess(t, _) => Some(t)
  }

  def JsonToReview(js : JsValue) : Review = {
    js.as[Review]
  }
}
