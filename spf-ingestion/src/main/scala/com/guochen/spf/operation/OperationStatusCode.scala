package com.guochen.spf.operation

import scala.util.Try

object OperationStatusCode extends Enumeration {
  type OperationStatusCode = Value
  val SUCCESS, WARNING, FAILURE = Value

  def contains(code: String): Boolean = Try(values.contains(OperationStatusCode(code))).isSuccess

  def apply(code: String): OperationStatusCode = withName(code.toUpperCase())
}
