package com.guochen.spf.operation

import com.guochen.spf.operation.OperationExitCode.OperationExitCode

case class OperationResult[+T](result: Option[T], exitCode: OperationExitCode, msg: String)
