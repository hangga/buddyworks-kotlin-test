package id.web.hangga

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.system.measureTimeMillis

class BuddyWorksExampleUnitTest {

    private fun printMemoryUsage(label: String): Long {
        System.gc() // Force garbage collection for more stable results
        Thread.sleep(100) // Allow time for GC to work
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        println("$label - Used Memory: $usedMemory MB")
        return usedMemory // Return the used memory value
    }

    @Test
    fun `coroutines mem test`() {
        // Measure memory before execution
        val beforeMemory = printMemoryUsage("Before")

        // Using threads
        val threads = List(10_000) {
            Thread {
                Thread.sleep(10)
            }.apply { start() }
        }
        threads.forEach { it.join() }
        val afterThreadsMemory = printMemoryUsage("After Threads")

        // Using coroutines
        runBlocking {
            val jobs = List(10_000) {
                launch {
                    delay(10)
                }
            }
            jobs.forEach { it.join() }
        }
        val afterCoroutinesMemory = printMemoryUsage("After Coroutines")

        // Ensure a measurable increase in memory usage
        val threadMemoryIncrease = max(0, afterThreadsMemory - beforeMemory)
        val coroutineMemoryIncrease = max(0, afterCoroutinesMemory - beforeMemory)

        // Assert that coroutines use less or equal memory compared to threads
        assertTrue(
            coroutineMemoryIncrease <= threadMemoryIncrease * 1.2,
            "Coroutines should be more memory-efficient or at least comparable to threads"
        )
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

        // Assertion: Ensure coroutines are not significantly slower than threads
        assertTrue(
            coroutineTime <= threadTime * 1.5,
            "Coroutines should not be significantly slower than threads"
        )
    }
}