package com.guochen.spf.operation

import scala.util.Try

object OperationExitCode extends Enumeration {
  type OperationExitCode = Value
  val SUCCESS, WARNING, FAILURE = Value

  def contains(code: String): Boolean = Try(values.contains(OperationExitCode(code))).isSuccess

  def apply(code: String): OperationExitCode = withName(code.toUpperCase())
}
