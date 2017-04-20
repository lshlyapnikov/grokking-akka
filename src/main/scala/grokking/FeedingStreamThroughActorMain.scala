package grokking

import akka.actor.{ActorRef, ActorSystem}
import akka.stream._
import akka.stream.scaladsl._

// https://jaceklaskowski.gitbooks.io/spray-the-getting-started/content/akka-streams-feeding_source_through_actor.html
// http://loicdescotte.github.io/posts/play-akka-streams-twitter/
object FeedingStreamThroughActorMain extends App {

  implicit val system = ActorSystem("FeedingStreamThroughActor")

  val fibonaci: Stream[BigInt] = {
    import Stream._
    def loop(a0: BigInt, a1: BigInt): Stream[BigInt] = a1 #:: loop(a1, a0 + a1)

    0 #:: loop(0, 1)
  }

  def publishFibonaciNumbers(n: Int)(actor: ActorRef): Unit = {
    fibonaci.take(n).foreach { n => actor ! n }
  }

  val source = Source.actorRef[BigInt](1000, OverflowStrategy.fail)
  implicit val m = ActorMaterializer()

  val ref: ActorRef = Flow[(BigInt, Long)].to(Sink.foreach { i => println(s"received: $i") }).runWith(source.zipWithIndex)

  Thread.sleep(100) // Need this sleep, does not work without it. Why???
  publishFibonaciNumbers(99)(ref)

  Thread.sleep(500)
  system.terminate()
}
