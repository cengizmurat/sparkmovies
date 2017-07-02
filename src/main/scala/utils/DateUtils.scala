package utils

import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by muratcengiz on 02/07/17.
  */
object DateUtils {

  val format = new SimpleDateFormat("yyyy-mm-dd")

  def StringToDate(str: String) : Date = format.parse(str)

}
