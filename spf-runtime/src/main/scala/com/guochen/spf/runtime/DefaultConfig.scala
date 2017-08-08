package com.guochen.spf.runtime

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

object DefaultConfig {
  ConfigFactory.parseMap(Map("a" -> "b").asJava)
}
