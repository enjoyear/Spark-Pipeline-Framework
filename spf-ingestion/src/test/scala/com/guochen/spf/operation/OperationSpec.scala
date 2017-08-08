package com.guochen.spf.operation

import com.guochen.spf.operation.checker.IntRangeChecker
import com.guochen.spf.operation.converter.StringToInt
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class OperationSpec extends FlatSpec {
  "operation chain" should "work" in {
    val intCast = StringToInt()
    val intRangeChecker = IntRangeChecker(1, 10)
    val operations: List[Operation[_, _]] = List(intCast, intRangeChecker)
    val cell = "2"

    println(operations.getClass)
  }

//  def process(cell: String, operations: List[Operation[_, _]]): Any = {
  //    if (operations.isEmpty)
  //      cell
  //    else {
  //      val operator = operations.head
  //      operator.transform(cell.asInstanceOf[])
  //    }
  //  }
}
