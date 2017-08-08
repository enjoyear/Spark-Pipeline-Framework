package com.guochen.spf.operation.checker

import com.guochen.spf.operation.ValidateOperation

case class IntRangeChecker(lowerBound: Int, upperBound: Int) extends ValidateOperation[Int] {
  override def validate(cell: Int, transformed: Int): Boolean = cell >= lowerBound && cell < upperBound
}
