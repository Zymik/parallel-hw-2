import org.junit.jupiter.api.Test
import java.util.concurrent.ForkJoinPool


class BfsTest {

    companion object {
        private const val TEST_COUNT = 1
        private const val THREAD_COUNT = 4

        private fun getCubeGraphBfsResult(size: Int): IntArray {
            val result = IntArray(size * size * size)
            for (i in 0 until size) {
                for (j in 0 until size) {
                    for (k in 0 until size) {
                        val pos = getPos(i, j, k, size)
                        result[pos] = i + j + k
                    }
                }
            }
            return result
        }

        private fun getPos(i: Int, j: Int, k: Int, size: Int): Int = i + j * size + k * size * size

        private val forkJoinPool = ForkJoinPool(THREAD_COUNT)
    }

    @Test
    fun testSeqBfs() {
        val bfsResult = getCubeGraphBfsResult(200)
        val result = seqBfs(0, CubeGraph(200))
        result.contentEquals(bfsResult)
    }

    @Test
    fun testForkJoinBfs() {
        val bfsResult = getCubeGraphBfsResult(200)
        val result = runForkJoinBfs(CubeGraph(200))
        result.contentEquals(bfsResult)
    }

    @Test
    fun performanceTest() {
        val graph = CubeGraph(500)
        var seqTime = 0L
        for (i in 1..TEST_COUNT) {
            println("Iteration $i sequential")
            seqTime += countMillis {
                seqBfs(0, graph)
            }
        }
        println("Seq bfs average time: ${seqTime.toDouble() / TEST_COUNT}")
        var parTime = 0L
        for (i in 1..TEST_COUNT) {
            println("Iteration $i parallel")
            parTime += countMillis {
                runForkJoinBfs(graph)
            }
        }
        println("Par bfs average time: ${parTime.toDouble() / TEST_COUNT} ")
        println("ParTime/SeqTime: ${seqTime.toDouble() / parTime.toDouble()}")
    }

    private fun runForkJoinBfs(graph: Graph): IntArray = forkJoinPool.submit(ParBfs(graph, 0)).join()

    private fun countMillis(func: () -> Unit): Long {
        val start = System.currentTimeMillis()
        func()
        return System.currentTimeMillis() - start
    }

    private fun IntArray.contentEquals(expected: IntArray) {
        assert(size == expected.size) { "Arrays have different size" }

        for (i in indices) {
            assert(get(i) == expected[i]) { "Different elements at index $i" }
        }
    }
}