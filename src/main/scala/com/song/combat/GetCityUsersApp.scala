package com.song.combat

import org.apache.spark.{SparkConf, SparkContext}


/**
  * 这个需求是获得同一个城市中里面有多少用户
  * 结构 13650332631 user0 name0 55 professional59 city53 male
  */

object GetCityUsersApp {

  def main(args: Array[String]): Unit = {
    val conf=new SparkConf().setMaster("local[2]").setAppName("GetCityUsersApp")

    val sc=new SparkContext(conf)

    val lines=sc.textFile("/Users/zhangsongdeshendun/data/user.txt")

    val pairs=lines.map(line=>(line.split(" ")(5),line.split(" ")(2)))

    val group=pairs.groupByKey()

    group.foreach(println)
  }

}
