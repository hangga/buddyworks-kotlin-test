package id.web.hangga

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class BuddyWorksExampleUnitTest {

    private fun printMemoryUsage(label: String) {
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        println("$label - Used Memory: ${usedMemory} MB")
    }

    @Test
    fun `coroutines mem test`(){
        printMemoryUsage("Before")

        val threads = List(10_000) {
            Thread {
                Thread.sleep(10)
            }.apply { start() }
        }
        threads.forEach { it.join() }
        printMemoryUsage("After Threads")

        runBlocking {
            val jobs = List(10_000) {
                launch {
                    delay(10)
                }
            }
            jobs.forEach { it.join() }
        }
        printMemoryUsage("After Coroutines")
    }

    @Test
    fun `coroutines time test`() {
        val threadTime = measureTimeMillis {
            val thread = Thread { Thread.sleep(100) }
            thread.start()
            thread.join()
        }

        val coroutineTime = measureTimeMillis {
            runBlocking {
                val job = launch {
                    delay(100)
                }
                job.join()
            }
        }

        println("Thread response time: $threadTime ms")
        println("Coroutine response time: $coroutineTime ms")
    }
}