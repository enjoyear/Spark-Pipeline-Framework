package com.guochen.spf.operation

import scala.util.Try

/**
  * Define different levels of exit code
  * SUCCESS < WARNING < FAILURE
  * Once FAILURE is met, following operations should stop
  */
object OperationExitCode extends Enumeration {
  type OperationExitCode = Value
  val SUCCESS, WARNING, FAILURE = Value

  def contains(code: String): Boolean = Try(values.contains(OperationExitCode(code))).isSuccess

  def apply(code: String): OperationExitCode = withName(code.toUpperCase())

  def max(code1: OperationExitCode, code2: OperationExitCode) = if (code1 >= code2) code1 else code2
}
