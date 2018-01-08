package com.song.algorithm

import org.apache.spark.{SparkConf, SparkContext}

/**
  * topN算法，从20 78 56 45 23 15 12 35 79 68 98 63 111 222 333 444 555，中找出top10
  */

object TopN {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("WordCount")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")

    val lines = sc.textFile("/Users/zhangsongdeshendun/data/topN.txt")

    val line = lines.flatMap(_.split(" "))

    val wordParis = line.map(word => (word.toInt, null))

    val result = wordParis.sortByKey(false).map(_._1).take(10)

    result.foreach(println)

  }
}
