package com.guochen.spf

import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner

import scala.collection.mutable.Stack

@RunWith(classOf[JUnitRunner])
class SampleScalaUnitTest extends FlatSpec with Matchers {
  "SampleScalaUnitTest" should "be executed" in {
    println("Running sample scala unit test")
    val stack = new Stack[Int]
    stack.push(1)
    stack.push(2)
    stack.pop() should be(2)
    stack.pop() should be(1)
  }
}
