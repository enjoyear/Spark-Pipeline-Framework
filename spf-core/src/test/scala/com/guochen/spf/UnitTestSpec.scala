package com.guochen.spf

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FlatSpec}

@RunWith(classOf[JUnitRunner])
abstract class UnitTestSpec extends FlatSpec with BeforeAndAfter
