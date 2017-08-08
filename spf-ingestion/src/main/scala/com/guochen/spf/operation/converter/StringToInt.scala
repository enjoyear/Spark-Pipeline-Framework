package com.guochen.spf.operation.converter

import com.guochen.spf.operation.TransformOperation

case class StringToInt() extends TransformOperation[String, Int] {
  override def transform(cell: String): Int = cell.toInt
}
