package com.song.algorithm

import org.apache.spark.{SparkConf, SparkContext}

/**
  * 这是一个wordcount程序，基于sparkcore
  */

object WordCount {

  def main(args: Array[String]): Unit = {
    val conf=new SparkConf().setMaster("local[2]").setAppName("WordCount")
    val sc=new SparkContext(conf)

    val lines=sc.textFile("/Users/zhangsongdeshendun/data/wordcount.txt")

    val line=lines.flatMap(_.split(" "))

    val wordParis=line.map(word=>(word,1))

    val result=wordParis.reduceByKey(_+_)

    result.foreach(println)

  }

}
