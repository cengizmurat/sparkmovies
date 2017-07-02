package utils.http

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import play.api.libs.json.{JsValue, Json}

/**
  * Created by muratcengiz on 02/07/17.
  */
object HttpUtils {

  def getContent(url: String) : Option[JsValue] = {
    val httpClient = new DefaultHttpClient()
    val httpResponse = httpClient.execute(new HttpGet(url))
    val entity = httpResponse.getEntity
    var content : Option[JsValue] = None
    if (entity != null) {
      val inputStream = entity.getContent
      val jsonString = scala.io.Source.fromInputStream(inputStream).mkString
      content = Some(Json.parse(jsonString))
      inputStream.close()
    }
    httpClient.close()
    return content
  }
}
