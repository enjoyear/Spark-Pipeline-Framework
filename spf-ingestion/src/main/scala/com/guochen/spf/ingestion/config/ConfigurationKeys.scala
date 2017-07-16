package com.guochen.spf.ingestion.config

object ConfigurationKeys {
  val SOURCE_ROOT = "source"
  val SOURCE = ConfigurationKeysSource

  val CONVERTER_ROOT = "converter"


  val CHECKER_ROOT = "checker"


  val PUBLISHER_ROOT = "publisher"
  val PUBLISHER = ConfigurationKeysPublisher
}

object ConfigurationKeysSource {
  val LOCATION = "location"
}

object ConfigurationKeysPublisher {
  val LOCATION = "location"
}