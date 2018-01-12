package com.song.combat.utils

import org.apache.commons.lang3.time.FastDateFormat
import java.util.Date

/**
  * 日期时间工具
  */

object DateUtils {

  val YYYYMMDDHHMMSS_FORMAT=FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")

  val TARGET_FORMAT=FastDateFormat.getInstance("yyyyMMddHHmmss")

  def getTime(time:String)={
    YYYYMMDDHHMMSS_FORMAT.parse(time).getTime
  }

  def parseToMinute(time:String)={
    TARGET_FORMAT.format(new Date(getTime(time)))
  }

  def main(args: Array[String]): Unit = {
    print(parseToMinute("2018-01-11 19:28:00"))
  }


}
