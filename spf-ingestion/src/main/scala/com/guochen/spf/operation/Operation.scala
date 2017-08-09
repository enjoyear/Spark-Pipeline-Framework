package com.guochen.spf.operation

import com.guochen.spf.operation.OperationExitCode.OperationExitCode

import scala.util.{Failure, Success, Try}

/*
  Operation can be chained one after another to perform a series of operations
  1. Call operate first
  2. Validate. Get error msg if fails
 */
trait Operation[FROM, TO] {
  def operate(cell: Any): OperationResult[TO] = {
    operate(OperationResult[Any](Some(cell), OperationExitCode.SUCCESS, ""))
  }

  def operate(prev: OperationResult[Any]): OperationResult[TO] = {
    if (prev.exitCode >= operationStopLevel) {
      return OperationResult[TO](None, prev.exitCode, prev.msg)
    }

    Try(prev.asInstanceOf[FROM]) match {
      case Success(from) => {
        Try(transform(from)) match {
          case Success(to) => {
            if (validate(from, to)) {
              OperationResult[TO](Some(to), OperationExitCode.SUCCESS, "")
            } else {
              OperationResult[TO](Some(to), validationImpactLevel, "")
            }
          }
          case Failure(throwable) => OperationResult[TO](None, OperationExitCode.FAILURE, throwable.getMessage)
        }
      }
      case Failure(throwable) => OperationResult[TO](None, OperationExitCode.FAILURE, throwable.getMessage)
    }
  }

  /**
    * This method defines when this operation can perform.
    * If previous operation ends with an exitCode >= stopLevel, then this operation should not start.
    *
    * @return
    */
  def operationStopLevel: OperationExitCode = OperationExitCode.WARNING

  protected def transform(cell: FROM): TO

  protected def validate(cell: FROM, transformed: TO): Boolean

  /**
    * This method defines the impact level of validation failure
    *
    * @return
    */
  def validationImpactLevel: OperationExitCode = OperationExitCode.FAILURE
}

/*
  ValidationOperation is doing an identity mapping and perform a validation afterwards
 */
trait ValidateOperation[T] extends Operation[T, T] {
  override def transform(cell: T): T = cell
}

/*
  TransformOperation is doing a transformation and validation is true by default
 */
trait TransformOperation[FROM, TO] extends Operation[FROM, TO] {
  override def validate(cell: FROM, transformed: TO): Boolean = true
}