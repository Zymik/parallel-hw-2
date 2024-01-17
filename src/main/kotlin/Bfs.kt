import java.util.concurrent.RecursiveTask
import java.util.concurrent.atomic.AtomicIntegerArray

fun seqBfs(start: Int, graph: Graph): IntArray {
    val queue = ArrayDeque<Int>()
    val result = IntArray(graph.size)
    val visited = BooleanArray(graph.size)
    visited[start] = true
    queue.addLast(start)
    while (queue.isNotEmpty()) {
        val id = queue.removeFirst()
        graph[id].forEach { i ->
            if (!visited[i]) {
                visited[i] = true
                result[i] = result[id] + 1
                queue.addLast(i)
            }
        }
    }
    return result
}

class ParBfs(private val graph: Graph, private val start: Int) : RecursiveTask<IntArray>() {
    override fun compute(): IntArray {
        val visited = AtomicIntegerArray(graph.size)
        visited.set(start, 1)
        val result = IntArray(graph.size)
        var frontier = IntArray(1) { start + 1 }
        var layer = 1
        while (frontier.isNotEmpty()) {
            val next = IntArray(frontier.size)
            PFor(frontier) { it, i ->
                if (it > 0) {
                    next[i] = graph[it - 1].size
                } else {
                    next[i] = 0
                }
            }.fork().join()
            val scanned = Scan(next).fork().join()
            val nextFrontier = IntArray(scanned.last())

            PFor(frontier) { _, i ->
                val front = frontier[i] - 1
                if (front >= 0) {
                    val startPos = if (i == 0) {
                        0
                    } else {
                        scanned[i]
                    }
                    graph[front].forEachIndexed { j, v ->
                        if (visited.compareAndSet(v, 0, 1)) {
                            nextFrontier[startPos + j] = v + 1
                            result[v] = layer
                        }
                    }
                }
            }.fork().join()
            layer++
            frontier = nextFrontier
        }
        return result
    }
}