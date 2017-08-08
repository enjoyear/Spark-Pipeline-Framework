package com.guochen.spf.operation

import scala.reflect.ClassTag

/*
  Operation can be chained one after another to perform a series of operations
  1. Call operate first
  2. Validate. Get error msg if fails
 */
trait Operation[FROM, TO] {
  def transform(cell: FROM): TO

  def validate(cell: FROM, transformed: TO): Boolean

  //def validationContinuationLevel

  //  def operate(cell: FROM) = {
  //    //val transformed = transform(cell)
  //  }

  val fromType = implicitly[ClassTag[FROM]].runtimeClass
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