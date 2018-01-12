package com.song.combat

import com.song.combat.bean.ClickLog
import com.song.combat.utils.DateUtils
import org.apache.spark.{SparkConf, SparkContext}

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

    val cleanData = lines.map(line => {
      val infos = line.split("\t")

      //      infos(2)=GET /class/112.html HTTP/1.1
      //      url=/class/112.html
      val url = infos(2).split(" ")(1)
      var courseId = 0;

      //把实战课程的课程编号拿到了
      if (url.startsWith("/class")) {
        val courseIdHTML = url.split("/")(2)
        courseId = courseIdHTML.substring(0, courseIdHTML.lastIndexOf(".")).toInt
      }

      ClickLog(infos(0), DateUtils.parseToMinute(infos(1)), courseId, infos(3).toInt, infos(4))

    }).filter(clicklog => clicklog.courseId != 0)

    cleanData.foreach(println)


  }

}
