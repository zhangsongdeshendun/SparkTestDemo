package com.song.algorithm

import org.apache.spark.{SparkConf, SparkContext}

/**
  * 先分组然后在进行排序，在取topN
  */

object TopNGroup {


  def main(args: Array[String]): Unit = {


    val conf=new SparkConf().setMaster("local[2]").setAppName("TopNGroup")

    val sc=new SparkContext(conf)

    sc.setLogLevel("ERROR")

    val lines = sc.textFile("/Users/zhangsongdeshendun/data/TopNGroup.txt")

    val pairs = lines.map(line=>(line.split(",")(0),line.split(",")(1).toInt))

    val group=pairs.groupByKey

    val result=group.map(pair=>(pair._1,pair._2.toList.sortWith(_>_).take(5)))

    result.foreach(println)

  }

}
