package com.song.app

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}


/**
  * 使用spark Stream 完成有状态的统计，并将结果写入到Mysql数据库中
  */

object ForeachRddApp {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("ForeachRddApp")

    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val lines = ssc.socketTextStream("localhost", 9999)

    val words = lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)

//    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()

  }

}
