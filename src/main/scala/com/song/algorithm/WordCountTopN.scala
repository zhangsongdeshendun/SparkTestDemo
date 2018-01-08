package com.song.algorithm

import org.apache.spark.{SparkConf, SparkContext}

/**
  * 先进行wordcount,然后根据每个word出现的次数进行topN排序
  */

object WordCountTopN {


  def main(args: Array[String]): Unit = {


    val conf=new SparkConf().setMaster("local[2]").setAppName("WordCountTopN")

    val sc=new SparkContext(conf)

    sc.setLogLevel("ERROR")

    val lines = sc.textFile("/Users/zhangsongdeshendun/data/wordcount.txt")

    val line = lines.flatMap(_.split(" "))

    val wordPairs = line.map(word => (word,1))

    val result=wordPairs.reduceByKey(_+_).map(pairs=>(pairs._2,pairs._1)).sortByKey(false).map(pairs=>(pairs._2,pairs._1))

    result.foreach(println)
  }


}
