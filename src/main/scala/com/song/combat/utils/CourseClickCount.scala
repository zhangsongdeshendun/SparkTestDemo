package com.song.combat.utils

/**
  *
  * @param day_course 对应hbase中的rowkey 20180111_123
  * @param click_count 对应20180111_123的点击总数
  */

case class CourseClickCount(day_course:String,click_count:Long)
