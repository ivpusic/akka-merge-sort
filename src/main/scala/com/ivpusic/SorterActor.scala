package com.example

import akka.pattern.{ask, pipe}
import akka.actor.{Props, Actor}
import akka.actor.Actor.Receive
import scala.concurrent.duration._
import akka.util.Timeout
import com.ivpusic.Messages._

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by ivpusic on 9/23/14.
 */
class SorterActor extends Actor {
  override def receive: Receive = {
    case MergeSort(list) =>
      implicit val ec: ExecutionContext = context.system.dispatchers.lookup("akka.actor.sorting-dispatcher")
      implicit val timeout = Timeout(5 seconds)

      // setup sorter actors
      val sorter1 = context.actorOf(SorterActor.build
        .withDispatcher("akka.actor.sorting-dispatcher"), name = "sorter_child1")
      val sorter2 = context.actorOf(SorterActor.build
        .withDispatcher("akka.actor.sorting-dispatcher"), name = "sorter_child2")
      val (first, second) = list.splitAt(list.size / 2)

      val fut1 = ask(sorter1, PartialMergeSort(first)).mapTo[List[Int]]
      val fut2 = ask(sorter2, PartialMergeSort(second)).mapTo[List[Int]]

      // fire sorting
      val resFuture: Future[(List[Int], List[Int])] = for {
        f1 <- fut1
        f2 <- fut2
      } yield (f1, f2)

      resFuture.map(result => {
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