package com.guochen.spf.operation

import com.guochen.spf.UnitTestSpec
import org.scalatest.GivenWhenThen

class OperationStatusCodeSpec extends UnitTestSpec with GivenWhenThen {
  behavior of "OperationStatusCode"

  it should "contains all elements" in {
    assert(OperationStatusCode.contains("success"))
    assert(OperationStatusCode.contains("warning"))
    assert(OperationStatusCode.contains("failure"))
  }

  it must "maintain order" in {
    println(OperationStatusCode.SUCCESS < OperationStatusCode.WARNING)
    println(OperationStatusCode.WARNING < OperationStatusCode.FAILURE)
  }
}
