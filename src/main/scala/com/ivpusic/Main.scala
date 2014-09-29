package com.example

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import com.ivpusic.Messages._

object Main {

  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem("sortersystem")
    val sorterActor = actorSystem.actorOf(SorterActor.build, name = "main_sorter")

    // generate random numbers
    val random = new scala.util.Random
    val list = (1 to 20000 map(_ => random.nextInt(20000))).toList

    // setup timeout and dispatcher
    implicit val timeout = Timeout.apply(10 seconds)
    import actorSystem.dispatcher

    // test normal sorting
    var startTime = System.currentTimeMillis()
    SortAlgorithm.mergeSort(list)
    var endTime = System.currentTimeMillis() - startTime
    println("Normal sorting: " + endTime)

    // test sorting using actors
    startTime = System.currentTimeMillis()
    sorterActor.ask(MergeSort(list.toList)).map(result => {
      endTime = System.currentTimeMillis() - startTime
      println("Actor sorting: " + endTime)
    })
  }
}
