package com.guochen.spf.operation

import com.guochen.spf.UnitTestSpec
import org.scalatest.GivenWhenThen

class OperationStatusCodeSpec extends UnitTestSpec with GivenWhenThen {
  behavior of "OperationStatusCode"

  it must "contains all elements" in {
    assert(OperationStatusCode.contains("success"))
    assert(OperationStatusCode.contains("warning"))
    assert(OperationStatusCode.contains("failure"))
  }
}
