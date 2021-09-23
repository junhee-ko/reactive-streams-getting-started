package jko.reactivestreamsgettingstarted

import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.function.Function

// Publisher -> Data1 -> Op1 -> Data2 -> Op2 -> Data3 -> Subscriber

// 1. map ( d1 -> f -> d2 )
// pub -> d1 -> mapPub -> d2 -> subscriber

class Operator {

    @Test
    fun operator() {
        val iterPub = iterPub(listOf(1, 2, 3, 4, 5))
        val logSub = logSub()
//        iterPub.subscribe(logSub)

        val mapPub = mapPub(iterPub) { s -> s * 10 }
        mapPub.subscribe(logSub)
    }

    private fun iterPub(iterable: Iterable<Int>): Publisher<Int> =
        Publisher { subscriber: Subscriber<in Int>? ->
            subscriber?.onSubscribe(object : Subscription {
                override fun request(n: Long) {
                    iterable.forEach { subscriber.onNext(it) }
                    subscriber.onComplete()
                }

                override fun cancel() {

                }
            })
        }

    private fun mapPub(pub: Publisher<Int>, f: Function<Int, Int>): Publisher<Int> {
        return Publisher<Int> { sub: Subscriber<in Int>? ->
            pub.subscribe(object : Subscriber<Int> {
                override fun onSubscribe(s: Subscription?) {
                    sub?.onSubscribe(s) // 중계
                }

                override fun onNext(t: Int) {
                    sub?.onNext(f.apply(t))
                }

                override fun onError(t: Throwable?) {
                    sub?.onError(t)
                }

                override fun onComplete() {
                    sub?.onComplete()
                }
            })
        }
    }

    private fun logSub() = object : Subscriber<Int> {
        override fun onSubscribe(s: Subscription?) {
            println("onSubscribe")

            s?.request(Long.MAX_VALUE)
        }

        override fun onNext(t: Int?) {
            println("onNext $t")
        }

        override fun onError(t: Throwable?) {
            println("onError")

        }

        override fun onComplete() {
            println("onComplete")
        }
    }
}
