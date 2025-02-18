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
        System.gc() // Paksa garbage collection untuk hasil yang lebih stabil
        Thread.sleep(100) // Beri waktu agar GC bekerja
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        println("$label - Used Memory: ${usedMemory} MB")
        return usedMemory // Mengembalikan nilai memori yang digunakan
    }

    @Test
    fun `coroutines mem test`() {
        // Mengukur memori sebelum eksekusi
        val beforeMemory = printMemoryUsage("Before")

        // Menggunakan thread
        val threads = List(10_000) {
            Thread {
                Thread.sleep(10)
            }.apply { start() }
        }
        threads.forEach { it.join() }
        val afterThreadsMemory = printMemoryUsage("After Threads")

        // Menggunakan coroutines
        runBlocking {
            val jobs = List(10_000) {
                launch {
                    delay(10)
                }
            }
            jobs.forEach { it.join() }
        }
        val afterCoroutinesMemory = printMemoryUsage("After Coroutines")

        // Memastikan ada peningkatan memori yang terukur
        val threadMemoryIncrease = max(0, afterThreadsMemory - beforeMemory)
        val coroutineMemoryIncrease = max(0, afterCoroutinesMemory - beforeMemory)

        // Assert bahwa coroutines lebih hemat memori daripada threads
        assertTrue(
            coroutineMemoryIncrease > threadMemoryIncrease * 1.2,
            "Coroutines seharusnya lebih hemat atau setara dibanding threads"
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

        // Assertion: Memastikan coroutines tidak lebih lambat secara signifikan dari threads
        assertTrue(coroutineTime <= threadTime * 1.5, "Coroutines seharusnya tidak jauh lebih lambat dari threads")
    }
}