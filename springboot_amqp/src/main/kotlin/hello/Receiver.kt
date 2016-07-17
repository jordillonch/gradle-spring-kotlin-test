package hello

import java.util.concurrent.CountDownLatch

class Receiver {

    val latch = CountDownLatch(1)

    fun receiveMessage(message: String) {
        println("Received <$message>")
        latch.countDown()
    }
}
