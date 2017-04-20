package grokking

import akka.stream._
import akka.stream.scaladsl._

import akka.{NotUsed, Done}
import akka.actor.ActorSystem
import akka.util.ByteString
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths

object FistStreamMain extends App {
  val source: Source[Int, NotUsed] = Source(1 to 10)

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  source.map { i => i * 2 }.runForeach { i => println(i) }(materializer)

  val fibonaci: Stream[Int] = {
    import Stream._
    def loop(a0: Int, a1: Int): Stream[Int] = a1 #:: loop(a1, a0 + a1)

    0 #:: loop(0, 1)
  }

  val source1: Source[BigInt, NotUsed] = Source.unfold((BigInt(0), BigInt(1))) { s =>
    Some(((s._2, s._1 + s._2), s._1))
  }

  source1.take(21).runForeach { i => println(i) }(materializer)

  val source3: Source[BigInt, NotUsed] = Source.unfold(BigInt(0)) { s =>
    Some((s + 1, s))
  }



  val source4: Source[BigInt, NotUsed] = Source.unfold(BigInt(0)) { s =>
    if (s < 4) Some((s + 1, s))
    else None
  }

  source3.zip(source4).runForeach { i => println(i) }(materializer)

  source1.zip(source3).take(99).runForeach { i => println(i) }(materializer)
}
