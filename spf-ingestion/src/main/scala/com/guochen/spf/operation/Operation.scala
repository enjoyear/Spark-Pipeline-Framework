package com.guochen.spf.operation

import com.guochen.spf.operation.OperationExitCode.OperationExitCode

import scala.util.{Failure, Success, Try}

/**
  * Operation can be chained one after another to perform a series of operations
  * Each operation decides to start or not based on the previous operation exit code and its own startLevel.
  * If its prevResult.exitCode <= startLevel, then current operation can start.
  *
  * Each operation ends with a exitCode based on validation result.
  * If the validation succeeds, the exitCode is SUCCESS; otherwise, it's defined by validationExitLevel
  *
  * @tparam FROM defines the type for the source of this operation
  * @tparam TO   defines the type for the output of this operation
  */
abstract class Operation[FROM, TO](settings: Map[String, String]) {

  import Operation._

  /**
    * The startLevel defines when this operation can perform.
    * An operation can start only when previous operation ends with an exitCode <= startLevel.
    * Otherwise, this operation will skip, so do the following operations.
    */
  val startLevel: OperationExitCode = {
    val startLevel: OperationExitCode = OperationExitCode(settings.getOrElse(ARG_START_LEVEL, "success"))
    require(startLevel < OperationExitCode.FAILURE, "Operation start level must be less than FAILURE")
    startLevel
  }

  /**
    * This exitLevel defines the exit code of a failed validation,
    * thus impact the following operations.
    */
  val validationFailureLevel: OperationExitCode = {
    val exitLevel: OperationExitCode = OperationExitCode(settings.getOrElse(ARG_VALIDATION_FAILURE_LEVEL, "FAILURE"))
    exitLevel
  }

  def process(cell: Any): OperationResult[TO] = {
    process(OperationResult[Any](Some(cell), OperationExitCode.SUCCESS, ""))
  }

  def process(prev: OperationResult[Any]): OperationResult[TO] = {
    if (prev.shouldStop) {
      //The operation stops at an earlier step in this chain
      return OperationResult[TO](None, prev.exitCode, prev.msg, shouldStop = true)
    }
    if (prev.exitCode > startLevel) {
      //We should stop when ExitCode > startLevel
      return OperationResult[TO](None, prev.exitCode,
        appendMessage(prev.msg, s"Stopped at ${this.toString}"), shouldStop = true)
    }

    if (prev.result.isEmpty) {
      throw new RuntimeException("This is impossible. All cases have been handled.")
    }

    Try(prev.result.get.asInstanceOf[FROM]) match {
      case Success(from) =>
        Try(transform(from)) match {
          case Success(to) =>
            if (validate(from, to)) {
              OperationResult[TO](Some(to), prev.exitCode, prev.msg)
            } else {
              //Validation fails
              OperationResult[TO](Some(to),
                OperationExitCode.max(validationFailureLevel, prev.exitCode),
                appendMessage(prev.msg, s"Failed at ${this.toString}"))
            }
          //This failure is a transform error. Thus always ExitCode.FAILURE
          case Failure(throwable) => OperationResult[TO](None, OperationExitCode.FAILURE,
            appendMessage(prev.msg, throwable.getMessage))
        }
      //This failure is an input casting error. Thus always ExitCode.FAILURE
      case Failure(throwable) => OperationResult[TO](None, OperationExitCode.FAILURE,
        appendMessage(prev.msg, throwable.getMessage))
    }
  }


  protected def transform(cell: FROM): TO

  protected def validate(cell: FROM, transformed: TO): Boolean

  private def appendMessage(prevMsg: String, newMsg: String): String = {
    prevMsg + " | " + newMsg
  }
}

object Operation {
  val ARG_START_LEVEL: String = "start_level"
  val ARG_VALIDATION_FAILURE_LEVEL: String = "validation_failure_level"
}

/**
  * ValidationOperation is doing an identity mapping and perform a validation afterwards
  *
  * @param args the args map
  * @tparam T the FROM and TO should have the same type for validation operation
  */
abstract class ValidateOperation[T](args: Map[String, String] = Map()) extends Operation[T, T](args) {
  override def transform(cell: T): T = cell
}

/**
  * TransformOperation is doing a transformation and validation is true by default
  *
  * @param args the args map
  * @tparam FROM defines the type for the source of this operation
  * @tparam TO   defines the type for the output of this operation
  */
abstract class TransformOperation[FROM, TO](args: Map[String, String] = Map()) extends Operation[FROM, TO](args) {
  override def validate(cell: FROM, transformed: TO): Boolean = true
}