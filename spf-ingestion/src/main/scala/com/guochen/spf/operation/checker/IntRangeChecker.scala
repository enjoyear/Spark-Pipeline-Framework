package com.guochen.spf.operation.checker

import com.guochen.spf.operation.ValidateOperation

case class IntRangeChecker(lowerBound: Int, upperBound: Int, args: Map[String, String] = Map()) extends ValidateOperation[Int](args) {
  override def validate(input: Int, transformed: Int): Boolean = input >= lowerBound && input < upperBound
}
