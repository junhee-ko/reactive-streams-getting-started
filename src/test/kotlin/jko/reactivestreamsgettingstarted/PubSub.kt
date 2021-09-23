package jko.reactivestreamsgettingstarted

import org.junit.jupiter.api.Test
import java.util.concurrent.Flow.*

class PubSub {

    @Test
    fun pubsub() {
        val iterable: Iterable<Int> = listOf(1, 2, 3, 4, 5)

        val publisher: Publisher<Int> = createPublisher(iterable)
        val subscriber: Subscriber<Int> = createSubscriber()

        publisher.subscribe(subscriber)
    }

    private fun createPublisher(iterable: Iterable<Int>): Publisher<Int> =
        Publisher { subscriber: Subscriber<in Int> ->
            val iterator: Iterator<Int> = iterable.iterator()

            subscriber.onSubscribe(object : Subscription {
                override fun request(n: Long) {
                    var count = n
                    while (count-- > 0) {
                        if (iterator.hasNext()) {
                            subscriber.onNext(iterator.next())
                        } else {
                            subscriber.onComplete()
                            break
                        }
                    }

                }

                override fun cancel() {
                }
            })
        }

    private fun createSubscriber() = object : Subscriber<Int> {
        var sunbscription: Subscription? = null

        override fun onSubscribe(subscription: Subscription?) {
            println("onSubscribe")
            this.sunbscription = subscription
            subscription?.request(1)
        }

        override fun onNext(item: Int?) {
            println("onNext $item")
            this.sunbscription?.request(1)

        }

        override fun onError(throwable: Throwable?) {
            println("onError")
        }

        override fun onComplete() {
            println("onComplete")
        }
    }
}
