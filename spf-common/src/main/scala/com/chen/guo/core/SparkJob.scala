package com.chen.guo.core

import com.typesafe.config.Config
import org.apache.spark.SparkContext

sealed trait SparkJobValidation {
  def &&(other: SparkJobValidation): SparkJobValidation = this match {
    case SparkJobValid => other
    case invalid => invalid
  }
}

case object SparkJobValid extends SparkJobValidation

case class SparkJobInvalid(reason: String) extends SparkJobValidation

trait SparkJob {
  def runJob(sc: SparkContext, jobConfig: Config): Any

  def validate(sc: SparkContext, config: Config): SparkJobValidation
}
