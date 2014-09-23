package com.example

import akka.pattern.{ask, pipe}
import akka.actor.{Props, Actor}
import akka.actor.Actor.Receive
import scala.concurrent.duration._
import akka.util.Timeout
import com.ivpusic.Messages._

import scala.concurrent.Future

/**
 * Created by ivpusic on 9/23/14.
 */
class SorterActor extends Actor {
  override def receive: Receive = {
    case MergeSort(list) =>
      import context.dispatcher
      implicit val timeout = Timeout(5 seconds)

      // setup sorter actors
      val sorter1 = context.actorOf(SorterActor.build
        .withDispatcher("akka.actor.sorting-dispatcher"), name = "sorter_child1")
      val sorter2 = context.actorOf(SorterActor.build
        .withDispatcher("akka.actor.sorting-dispatcher"), name = "sorter_child2")
      val (first, second) = list.splitAt(list.size / 2)

      // fire sorting
      val f: Future[(List[Int], List[Int])] = for {
        f <- (sorter1 ? PartialMergeSort(first)).mapTo[List[Int]]
        s <- (sorter2 ? PartialMergeSort(second)).mapTo[List[Int]]
      } yield (f, s)

      f.map(result => {
        val (firstSorted, secondSorted) = result
        SortAlgorithm.merge(firstSorted, secondSorted)
      }).pipeTo(sender())

    case PartialMergeSort(x) =>
      sender() ! SortAlgorithm.mergeSort(x)
  }
}

object SorterActor {
  def build = Props(new SorterActor)
}