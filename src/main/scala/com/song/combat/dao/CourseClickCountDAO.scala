package com.song.combat.dao

import com.song.combat.utils.CourseClickCount

import scala.collection.mutable.ListBuffer

/**
  * 实战课程点击数数据访问层
  */

object CourseClickCountDAO {

  val tableName="course_clickcount"
  val cf="info"
  val qualifer="click_count"

  /**
    * 保存数据到hbase
    * @param list CourseClickCount的集合
    */
  def save(list:ListBuffer[CourseClickCount]): Unit ={


  }

  /**
    * 根据rowkey查询值
    * @param day_course
    * @return
    */
  def count(day_course:String):Long={

    0l

  }

}
