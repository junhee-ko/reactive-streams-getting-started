package jko.reactivestreamsgettingstarted

import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

val logger: Logger = LoggerFactory.getLogger("")

// iterPub -> [Data1] -> mapPub -> [Data2] -> logSub
// iterPub -> [Data1] -> mapPub -> [Data2] -> mapPub2 -> [Data3] -> logSub
fun main() {
    val iterPub = iterPub(
        list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    )
    val mapPub = mapPub(
        publisher = iterPub,
        f = { s -> s?.times(10) }
    )
//    val mapPub2 = mapPub(
//        publisher = mapPub,
//        f = { s -> s?.times(-1) }
//    )
    val sub = logSub()

    mapPub.subscribe(sub)
}

private fun logSub(): Subscriber<Int> {
    return object : Subscriber<Int> {
        override fun onSubscribe(s: Subscription) {
            logger.info("logSub: onSubscribe")
            s.request(Int.MAX_VALUE.toLong())
        }

        override fun onNext(t: Int?) {
            logger.info("logSub: onNext: $t")
        }

        override fun onError(t: Throwable?) {
            logger.info("logSub: onError: $t")
        }

        override fun onComplete() {
            logger.info("logSub: onComplete")
        }
    }
}

private fun mapPub(
    publisher: Publisher<Int>,
    f: (val1: Int?) -> Int?,
): Publisher<Int> {
    val publisher = object : Publisher<Int> {
        override fun subscribe(subscriber: Subscriber<in Int>) {
            val sub = object : Subscriber<Int> {
                override fun onSubscribe(s: Subscription) {
                    logger.info("mapPub: onSubscribe")
                    subscriber.onSubscribe(s)
                }

                override fun onNext(t: Int?) {
                    logger.info("mapPub: onNext")
                    val appliedValue = f(t)
                    subscriber.onNext(appliedValue)
                }

                override fun onError(t: Throwable?) {
                    logger.info("mapPub: onError")
                    subscriber.onError(t)
                }

                override fun onComplete() {
                    logger.info("mapPub: onComplete")
                    subscriber.onComplete()
                }
            }

            publisher.subscribe(sub)
        }
    }

    return publisher
}

private fun iterPub(
    list: List<Int>,
): Publisher<Int> {
    val publisher = object : Publisher<Int> {
        override fun subscribe(subscriber: Subscriber<in Int>) {
            subscriber.onSubscribe(object : Subscription {
                override fun request(n: Long) {
                    logger.info("iterPub: request")
                    try {
                        list.forEach { subscriber.onNext(it) }
                        subscriber.onComplete()
                    } catch (e: Exception) {
                        subscriber.onError(e)
                    }
                }

                override fun cancel() {
                }
            })
        }
    }

    return publisher
}


