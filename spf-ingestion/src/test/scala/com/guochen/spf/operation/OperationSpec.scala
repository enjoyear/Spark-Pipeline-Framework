package com.guochen.spf.operation

import com.guochen.spf.UnitTestSpec
import com.guochen.spf.operation.checker.IntRangeChecker
import com.guochen.spf.operation.converter.StringToInt

class OperationSpec extends UnitTestSpec {
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

  behavior of "A chain of operations"

  it should "work in good example" in {
    val intCast = StringToInt()
    val intRangeChecker = IntRangeChecker(1, 5)
    val opResult = Operation.process("1", List(intCast, intRangeChecker))
    assertResult(OperationResult[Int](Some(1), OperationExitCode.SUCCESS, "", shouldStop = false)) {
      opResult
    }
  }

  it should "have correct result when validation fails" in {
    val intCast = StringToInt()
    val intRangeChecker = IntRangeChecker(2, 5)
    val opResult = Operation.process("1", List(intCast, intRangeChecker))
    assertResult(OperationResult[Int](Some(1), OperationExitCode.FAILURE, "IntRangeChecker(2,5,Map()) validation failed for 1", shouldStop = false)) {
      opResult
    }
  }

  it should "have correct result when transformation fails" in {
    val intCast = StringToInt()
    val intRangeChecker1 = IntRangeChecker(1, 5)
    val intRangeChecker2 = IntRangeChecker(0, 10)
    val opResult = Operation.process("hi", List(intCast, intRangeChecker1, intRangeChecker2))
    assertResult(OperationResult[Int](None, OperationExitCode.FAILURE, "Transformation StringToInt(Map()) failed: For input string: \"hi\" | Stopped at IntRangeChecker(1,5,Map())", shouldStop = true)) {
      opResult
    }
  }

  it should "have correct result when validation fails with a continuable level" in {
    val intCast = StringToInt()
    val intRangeChecker1 = IntRangeChecker(1, 5, args = Map(Operation.ARG_VALIDATION_FAILURE_LEVEL -> "warning"))
    val intRangeChecker2 = IntRangeChecker(0, 10, args = Map(Operation.ARG_START_LEVEL -> "warning"))
    val opResult = Operation.process("8", List(intCast, intRangeChecker1, intRangeChecker2,
      new TransformOperation[Int, Int](Map(Operation.ARG_START_LEVEL -> "warning")) {
        override protected def transform(input: Int): Int = input + 10
      }))
    assertResult(OperationResult[Int](Some(18), OperationExitCode.WARNING, "IntRangeChecker(1,5,Map(validation_failure_level -> warning)) validation failed for 8", shouldStop = false)) {
      opResult
    }
  }
}
