package com.guochen.spf.operation

import com.guochen.spf.operation.OperationStatusCode.OperationStatusCode

case class OperationResult[T](result: T, code: OperationStatusCode, msg: String)
