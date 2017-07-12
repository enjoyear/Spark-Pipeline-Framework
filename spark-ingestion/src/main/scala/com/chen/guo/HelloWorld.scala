package com.chen.guo

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}


object HelloWorld extends App {
  private val config: Config = ConfigFactory.parseFile(new File("/Users/chguo/repos/enjoyear/Spark-Pipeline-Framework/spark-ingestion/src/main/resources/com/chen/guo/simple.conf"))

  println(config.getString("Hi"))

}