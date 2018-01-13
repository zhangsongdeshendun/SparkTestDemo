package com.song.combat

import com.song.combat.dao.{CourseSearchClickCountDAO}
import com.song.combat.domain.{ClickLog, CourseSearchClickCount}
import com.song.combat.utils.DateUtils
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
  * 用来完成实战2，用引流过来的实战课程的点击量
  *
  * 72.98.132.29	2018-01-09 12:01:00	"GET /class/112.html HTTP/1.1"	404	http://www.cn.bing.com/search?q=Hadoop 基础
  */

object HandleUserSearchClickApp {


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

    //统计从搜索引擎过来今天到现在为止实战课程的访问量
    cleanData.map(x => {
      val referer = x.referer.replaceAll("//", "/")
      val splits = referer.split("/")
      var host = ""
      if (splits.length > 2) {
        host = splits(1)
      }
      (host, x.courseId, x.time)

    }).filter(_._1 != "").map(x => {
      (x._3.substring(0, 8) + "_" + x._1 + "_" + x._2, 1)
    }).reduceByKey(_ + _).foreachPartition(part => {
      val list = new ListBuffer[CourseSearchClickCount]
      part.foreach(x => {
        list.append(CourseSearchClickCount(x._1, x._2))
      })
      CourseSearchClickCountDAO.save(list)
    })

  }

}
