package com.guochen.spf

import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner

import scala.collection.mutable.Stack

@RunWith(classOf[JUnitRunner])
class SampleScalaIntegrationTest extends FlatSpec with Matchers {
  "SampleScalaIntegrationTest" should "be executed" in {
    println("Running sample scala integration test")
    val stack = new Stack[Int]
    stack.push(1)
    stack.push(2)
    stack.pop() should be(2)
    stack.pop() should be(1)
  }
}
