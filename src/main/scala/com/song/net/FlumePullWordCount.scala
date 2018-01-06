package com.song.net

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.flume.FlumeUtils

/**
  * Spark Streaming整合Flume的第二种方式 采用pull （这种方式是spark从自定义的sink中拉数据，数据会在spark中有三个副本，数据不容易丢失）
  *
  */

object FlumePullWordCount {

  def main(args: Array[String]): Unit = {

    if(args.length!=2){
      System.err.println("Ussage:参数错误")
      System.exit(1)
    }

    val Array(hostname,port)=args//hostname=0.0.0.0 port=41414

    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("FlumePullWordCount")

    val ssc = new StreamingContext(sparkConf, Seconds(5))


    val flumeStream=FlumeUtils.createPollingStream(ssc,hostname,port.toInt)

    flumeStream.map(x=>new String(x.event.getBody.array()).trim)
      .flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).print()


    ssc.start()
    ssc.awaitTermination()

  }

}
