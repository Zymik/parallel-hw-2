import org.junit.jupiter.api.Test
import java.util.concurrent.ForkJoinPool


class BfsTest {

    companion object {
        private const val TEST_COUNT = 5
        private const val THREAD_COUNT = 4

        data class CubicGraph(val graph: Graph, val bfsResult: IntArray)

        private fun buildCubicGraph(size: Int): CubicGraph {
            val result = IntArray(size * size * size)
            val graphList = Array(size * size * size) { IntArray(0) }
            for (i in 0 until size) {
                for (j in 0 until size) {
                    for (k in 0 until size) {
                        val pos = getPos(i, j, k, size)
                        result[pos] = i + j + k
                        val list = mutableListOf<Int>()
                        if (i != 0) {
                            list += getPos(i - 1, j, k, size)
                        }
                        if (j != 0) {
                            list += getPos(i, j - 1, k, size)
                        }
                        if (k != 0) {
                            list += getPos(i, j, k - 1, size)
                        }

                        if (i != size - 1) {
                            list += getPos(i + 1, j, k, size)
                        }
                        if (j != size - 1) {
                            list += getPos(i, j + 1, k, size)
                        }
                        if (k != size - 1) {
                            list += getPos(i, j, k + 1, size)
                        }
                        graphList[pos] = list.toIntArray()
                    }
                }
            }
            return CubicGraph(Graph(graphList), result)
        }

        private fun getPos(i: Int, j: Int, k: Int, size: Int): Int = i + j * size + k * size * size

        private val forkJoinPool = ForkJoinPool(THREAD_COUNT)
    }

    @Test
    fun testSeqBfs() {
        val graph = buildCubicGraph(200)
        val result = seqBfs(0, graph.graph)
        result.contentEquals(graph.bfsResult)
    }

    @Test
    fun testForkJoinBfs() {
        val graph = buildCubicGraph(200)
        val result = runForkJoinBfs(graph.graph)
        result.contentEquals(graph.bfsResult)
    }

    @Test
    fun performanceTest() {
        val graph = buildCubicGraph(500)
        var seqTime = 0L
        for (i in 1..TEST_COUNT) {
            println("Iteration $i sequential")
            seqTime += countMillis {
                seqBfs(0, graph.graph)
            }
        }
        println("Seq bfs average time: ${seqTime.toDouble() / TEST_COUNT}")
        var parTime = 0L
        for (i in 1..TEST_COUNT) {
            println("Iteration $i parallel")
            parTime += countMillis {
                runForkJoinBfs(graph.graph)
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