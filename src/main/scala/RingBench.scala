import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import akka.pattern.gracefulStop

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object RingBench {
  def main(args: Array[String]) {
    implicit val system = ActorSystem("RingBenchmarkInAkka")
    val n = 100
    val m = 10
    val first = RingLink(n)


    // set timeout to be 1000 seconds.
    implicit val timeout = Timeout(1000 seconds)
    val start = System.currentTimeMillis()
    implicit val ec = system.dispatcher

    // send message to ring and wait to finish.
    first ? (first, m) onSuccess {
      case 'done =>
        println("[main] received the end of benchmark. killing the ring...")
        val stopped: Future[Boolean] = gracefulStop(first, timeout.duration)
        Await.result(stopped, timeout.duration)

        system.terminate()
        val end = System.currentTimeMillis()
        println("[main] ring benchmark for %d processes and %d rounds = %d milliseconds" format(n, m, end - start))
    }

  }
}