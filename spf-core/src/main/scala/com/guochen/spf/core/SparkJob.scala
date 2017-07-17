package com.guochen.spf.core

import com.typesafe.config.Config
import org.apache.spark.sql.SparkSession

sealed trait SparkJobValidation {
  def &&(other: SparkJobValidation): SparkJobValidation = this match {
    case SparkJobValid => other
    case invalid => invalid
  }
}

case object SparkJobValid extends SparkJobValidation

case class SparkJobInvalid(msg: String) extends SparkJobValidation

abstract class SparkJob(config: Config) {
  def validate(ss: SparkSession): SparkJobValidation

  def run(ss: SparkSession): Any
}
