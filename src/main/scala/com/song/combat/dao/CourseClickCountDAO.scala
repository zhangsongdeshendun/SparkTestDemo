package com.song.combat.dao

import com.song.combat.domain.CourseClickCount
import com.song.spark.HBaseUtils
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer


/**
  * 实战课程点击数数据访问层
  */

object CourseClickCountDAO {

  val tableName = "course_clickcount"
  val cf = "info"
  val qualifer = "click_count"

  /**
    * 保存数据到hbase
    *
    * @param list CourseClickCount的集合
    */
  def save(list: ListBuffer[CourseClickCount]): Unit = {

    val table = HBaseUtils.getInstance().getTable(tableName)

    for (ele <- list) {
      table.incrementColumnValue(Bytes.toBytes(ele.day_course),
        Bytes.toBytes(cf),
        Bytes.toBytes(qualifer),
        ele.click_count)
    }

  }

  /**
    * 根据rowkey查询值
    *
    * @param day_course
    * @return
    */
  def count(day_course: String): Long = {
    val table = HBaseUtils.getInstance().getTable(tableName)

    val get = new Get(Bytes.toBytes(day_course))
    val value = table.get(get).getValue(cf.getBytes, qualifer.getBytes)
    if (value == 0) {
      0l
    } else {
      Bytes.toLong(value)
    }

  }

  def main(args: Array[String]): Unit = {
    val list = new ListBuffer[CourseClickCount]
    list.append(CourseClickCount("20170112_8", 8))
    list.append(CourseClickCount("20170112_9", 9))
    list.append(CourseClickCount("20170112_1", 100))

    save(list)

    print(count("20170112_8") + " " + count("20170112_9") + "   " + count("20170112_1"))
  }

}
