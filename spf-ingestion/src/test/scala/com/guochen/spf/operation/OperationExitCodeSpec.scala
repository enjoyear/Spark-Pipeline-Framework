package com.guochen.spf.operation

import com.guochen.spf.UnitTestSpec
import org.scalatest.GivenWhenThen

class OperationExitCodeSpec extends UnitTestSpec with GivenWhenThen {
  behavior of "OperationExitCode"

  it should "contains all elements" in {
    assert(OperationExitCode.contains("success"))
    assert(OperationExitCode.contains("warning"))
    assert(OperationExitCode.contains("failure"))
  }

  it must "maintain order" in {
    assert(OperationExitCode.SUCCESS < OperationExitCode.WARNING)
    assert(OperationExitCode.WARNING < OperationExitCode.FAILURE)
  }

  it can "compare" in {
    assert(OperationExitCode.max(OperationExitCode.SUCCESS, OperationExitCode.WARNING) == OperationExitCode.WARNING)
  }
}
