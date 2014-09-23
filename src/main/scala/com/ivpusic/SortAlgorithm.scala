package com.example

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.util.control.TailCalls.TailRec

/**
 * Created by ivpusic on 9/23/14.
 */
object SortAlgorithm {
  @tailrec
  def merge(left: List[Int], right: List[Int], collector: List[Int] = List()): List[Int] = {
    (left, right) match {
      case (List(), _) => collector ++ right
      case (_, List()) => collector ++ left
      case (firstL :: l1, firstR :: r1) =>
        if (firstL < firstR) merge(l1, right, collector :+ firstL)
        else merge(left, r1, collector :+ firstR)
    }
  }

  def mergeSort(list: List[Int]): List[Int] = {
    val middle = list.length / 2

    // edge case
    if (middle == 0) list
    else {
      val (left, right) = list.splitAt(middle)
      merge(mergeSort(left), mergeSort(right))
    }
  }
}
