package grokking

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream._
import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import akka.util.ByteString

import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths
import java.util.concurrent.CountDownLatch

// https://jaceklaskowski.gitbooks.io/spray-the-getting-started/content/akka-streams-feeding_source_through_actor.html
object FeedingStreamThroughActorMain extends App {

  implicit val system = ActorSystem("FeedingStreamThroughActor")

  val fibonaci: Stream[Int] = {
    import Stream._
    def loop(a0: Int, a1: Int): Stream[Int] = a1 #:: loop(a1, a0 + a1)

    0 #:: loop(0, 1)
  }

  //  fibonaci.take(5).foreach(println)

  def run(actor: ActorRef): Unit = {
    import system.dispatcher
    Future {
      Thread.sleep(100) // WHY? It does not work without this sleep
      fibonaci.take(21).foreach { n => actor ! n }
    }
  }

  val source = Source.actorRef[Int](0, OverflowStrategy.fail).mapMaterializedValue { ref => println(ref); run(ref) }
  implicit val m = ActorMaterializer()

  source.runForeach { int ⇒
    println(s"received: $int")
  }(m)

  //  system.terminate()
}
