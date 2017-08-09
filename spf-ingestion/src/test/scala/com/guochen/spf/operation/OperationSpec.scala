package com.guochen.spf.operation

import com.guochen.spf.UnitTestSpec
import com.guochen.spf.operation.checker.IntRangeChecker
import com.guochen.spf.operation.converter.StringToInt

class OperationSpec extends UnitTestSpec {
  "operation chain" should "work" in {
    val intCast = StringToInt()
    val intRangeChecker = IntRangeChecker(1, 10)
    val operations: List[Operation[_, _]] = List(intCast, intRangeChecker)
    val cell = "a"

    //process(cell, operations)
  }

  def process(cell: Any, operations: List[Operation[_, _]]): Any = {
    if (operations.isEmpty)
      cell
    else {
      val operator = operations.head
      process(operator.operate(cell), operations.tail)
    }
  }
}
