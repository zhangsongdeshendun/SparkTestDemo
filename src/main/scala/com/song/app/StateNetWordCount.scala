package com.song.app

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}


/**
  * 使用spark stream完成有状态统计
  */

object StateNetWordCount {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("StateNetWordCount")

    val ssc = new StreamingContext(sparkConf, Seconds(5))
    //如果使用了stateful的算子，必须要设置checkpoint,在生产环境吧地址设置到hdfs的某个目录
    ssc.checkpoint("/Users/zhangsongdeshendun/Desktop/wen")

    val lines = ssc.socketTextStream("localhost", 6789)

    val words = lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word, 1))
    val state = pairs.updateStateByKey[Int](updateFunction _)


    state.print()

    ssc.start()
    ssc.awaitTermination()

  }

  /**
    * 把当前的数据去更新已有的或者老的数据
    *
    * @param currentValues
    * @param preValue
    */
  def updateFunction(currentValues: Seq[Int], preValue: Option[Int]): Option[Int] = {
    val current = currentValues.sum
    val pre = preValue.getOrElse(0)
    Some(current + pre)
  }

}
