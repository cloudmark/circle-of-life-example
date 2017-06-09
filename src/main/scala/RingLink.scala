import akka.actor.{Actor, ActorRef, ActorSystem, Kill, Props}

object RingLink {
  def apply(n: Int)(implicit system: ActorSystem): ActorRef = {
    system.actorOf(Props(new RingLink(n)), s"RingLink-$n")
  }
}


class RingLink(n: Int) extends Actor {
  println(s"Created RingLink $n")

  def receive: Receive = {
    case msg@(first: ActorRef, numberOfMessages: Int) => {
      if (n == 0) {
        context.become({
          case 0 => sender ! 'done
          case current: Int => first forward (current - 1)
        })
        first forward numberOfMessages
      } else {
        val next = context.actorOf(Props(new RingLink(n - 1)), s"RingLink-${n - 1}")
        next forward msg
        context.become({
          case m =>
            println(s"RingLink $n received message $m")
            next forward m
        })

      }
    }
  }
}


