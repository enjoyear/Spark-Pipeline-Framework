package com.guochen.spf.operation

import com.guochen.spf.UnitTestSpec
import org.scalatest.GivenWhenThen

class OperationExitCodeSpec extends UnitTestSpec with GivenWhenThen {
  behavior of "OperationStatusCode"

  it should "contains all elements" in {
    assert(OperationExitCode.contains("success"))
    assert(OperationExitCode.contains("warning"))
    assert(OperationExitCode.contains("failure"))
  }

  it must "maintain order" in {
    println(OperationExitCode.SUCCESS < OperationExitCode.WARNING)
    println(OperationExitCode.WARNING < OperationExitCode.FAILURE)
  }
}
