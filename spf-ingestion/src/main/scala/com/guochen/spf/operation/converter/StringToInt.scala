package com.guochen.spf.operation.converter

import com.guochen.spf.operation.TransformOperation

case class StringToInt(args: Map[String, String] = Map()) extends TransformOperation[String, Int](args) {
  override def transform(cell: String): Int = cell.toInt
}
