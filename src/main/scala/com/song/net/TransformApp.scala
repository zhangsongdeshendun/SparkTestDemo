package com.song.net

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 黑名单过滤
  */

object TransformApp {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("TransformApp")

    val ssc = new StreamingContext(sparkConf, Seconds(1))



    val black_rdd=ssc.sparkContext.parallelize(Array("song","tian")).map(x=>(x,true))

    val lines = ssc.socketTextStream("localhost", 6789)

    val click_log=lines.map(x=>(x.split(",")(0),x)).transform(rdd =>{
      rdd.leftOuterJoin(black_rdd)
        .filter(x=>x._2._2.getOrElse(false)==true)
        .map(x=>x._2._1)
    })

    click_log.print()

    //    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()

  }

}
