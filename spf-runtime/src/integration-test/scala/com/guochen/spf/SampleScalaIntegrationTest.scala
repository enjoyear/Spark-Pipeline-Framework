package com.guochen.spf.runtime.scheduler

import org.scalatest._
import scala.collection.mutable.Stack

class SampleScalaIntegrationTest extends FlatSpec with Matchers {
  "SampleScalaIntegrationTest" should "be executed" in {
    println("Running sample scala integration test")
    val stack = new Stack[Int]
    stack.push(1)
    stack.push(2)
    stack.pop() should be(1)
    stack.pop() should be(1)
  }
}
