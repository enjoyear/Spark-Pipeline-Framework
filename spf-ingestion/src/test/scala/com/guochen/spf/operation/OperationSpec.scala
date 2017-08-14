package com.guochen.spf.operation

import com.guochen.spf.UnitTestSpec
import com.guochen.spf.operation.checker.IntRangeChecker
import com.guochen.spf.operation.converter.StringToInt

class OperationSpec extends UnitTestSpec {
  behavior of "A chain of operations"

  it should "work" ignore {
    val intCast = StringToInt()
    val intRangeChecker = IntRangeChecker(1, 10)
    val operations: List[Operation[_, _]] = List(intCast, intRangeChecker)
    val cell = "1"

    var res = operations.head.process(cell)
    for (operation <- operations.tail) {
      res = operation.process(res)
    }
    println(res)
  }

  behavior of "Operation constructor"

  it should "accept a map" in {
    val intCast = StringToInt(Map(Operation.ARG_START_LEVEL -> "warning"))

    assertResult(OperationExitCode.WARNING) {
      intCast.startLevel
    }

    assertResult(OperationExitCode.FAILURE) {
      //The default validationFailureLevel should be FAILURE
      intCast.validationFailureLevel
    }
  }

  it can "not allow FAILURE as a start level" in {
    assertThrows[IllegalArgumentException] {
      StringToInt(Map(Operation.ARG_START_LEVEL -> "failure"))
    }
  }
}
