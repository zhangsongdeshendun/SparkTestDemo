package com.song.combat

import com.song.combat.bean.{ClickLog, CourseClickCount}
import com.song.combat.dao.CourseClickCountDAO
import com.song.combat.utils.DateUtils
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
  * 用来测试数据清洗
  *
  * 72.98.132.29	2018-01-09 12:01:00	"GET /class/112.html HTTP/1.1"	404	http://www.cn.bing.com/search?q=Hadoop 基础
  */

object HandleUserLogApp {


  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("HandleUserLogApp")

    val sc = new SparkContext(conf)

    val lines = sc.textFile("/Users/zhangsongdeshendun/data/access.log")
    //第二步：数据清洗
    val cleanData = lines.map(line => {
      val infos = line.split("\t")
      val url = infos(2).split(" ")(1)
      var courseId = 0;
      //把实战课程的课程编号拿到了
      if (url.startsWith("/class")) {
        val courseIdHTML = url.split("/")(2)
        courseId = courseIdHTML.substring(0, courseIdHTML.lastIndexOf(".")).toInt
      }
      ClickLog(infos(0), DateUtils.parseToMinute(infos(1)), courseId, infos(3).toInt, infos(4))
    }).filter(clicklog => clicklog.courseId != 0)

    //把数据写入到hbase中
    val pairs = cleanData.map(clicklog => {
      (clicklog.time.substring(0, 8) + "_" + clicklog.courseId, 1)
    }).reduceByKey(_ + _).foreachPartition(part => {
      val list = new ListBuffer[CourseClickCount]
      part.foreach(x => {
        list.append(CourseClickCount(x._1, x._2))
      })
      CourseClickCountDAO.save(list)
    })





  }

}
