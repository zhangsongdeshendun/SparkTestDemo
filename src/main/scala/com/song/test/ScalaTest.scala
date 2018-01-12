package com.song.test


import scala.collection.mutable.ListBuffer

/**
  * 这是一个Scala测试类
  */

object ScalaTest {

  def main(args: Array[String]): Unit = {
    val list=new ListBuffer[String]
    list.append("tian")
    print(list.size)

  }

}
