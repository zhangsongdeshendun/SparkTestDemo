package com.song.combat

import com.song.combat.domain.{ClickLog, CourseClickCount}
import com.song.combat.dao.CourseClickCountDAO
import com.song.combat.utils.DateUtils
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils

import scala.collection.mutable.ListBuffer


/**
  * 接收来自kafka的用户日志信息，进行实时处理
  *
  *72.98.132.29	2018-01-09 12:01:00	"GET /class/112.html HTTP/1.1"	404	http://www.cn.bing.com/search?q=Hadoop 基础
  *
  */

object HandleStreamUserLogApp {

  def main(args: Array[String]): Unit = {
    //两个参数是localhost:9092 streamtopic3
    if (args.length != 2) {
      System.err.println("Usage: KafkaDirectWordCount <brokers> <topics>")
      System.exit(1)
    }

    val Array(brokers, topics) = args

    val sparkConf = new SparkConf().setAppName("KafkaStringAppp").setMaster("local[2]")

    val ssc = new StreamingContext(sparkConf, Seconds(60))

    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)

    // TODO... Spark Streaming如何对接Kafka
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet
    )

    // TODO... 自己去测试为什么要取第二个
    //    messages.map(_._2).count().print()
    //数据清洗
    val logs = messages.map(_._2)
    val cleanData = logs.map(line => {
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

    //    cleanData.print()

    cleanData.map(x => {
      //HBase rowKey设计：20170101_88
      (x.time.substring(0, 8) + "_" + x.courseId, 1)
    }).reduceByKey(_ + _).foreachRDD(rdd => {
      rdd.foreachPartition(partitionRecords => {
        var list = new ListBuffer[CourseClickCount]
        partitionRecords.foreach(pair => {
          list.append(CourseClickCount(pair._1, pair._2))
        })

        CourseClickCountDAO.save(list)
      })

    })


    ssc.start()
    ssc.awaitTermination()
  }

}
