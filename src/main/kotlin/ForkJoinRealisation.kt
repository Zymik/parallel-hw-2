import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.RecursiveAction
import java.util.concurrent.RecursiveTask


var block = 5000

class PFor(
    private val array: IntArray,
    private val l: Int = 0,
    private val r: Int = array.size,
    val func: (Int, Int) -> Unit
) :
    RecursiveTask<Unit>() {
    override fun compute() {
        if (r - l < 1000) {
            for (i in l until r) {
                func(array[i], i)
            }
            return
        }
        val m = (l + r) / 2
        invokeAll(
            PFor(array, l, m, func),
            PFor(array, m, r, func)
        )
    }
}

suspend fun IntArray.pFor(l: Int = 0, r: Int = size, func: (Int, Int) -> Unit) {
    if (r - l < 1000) {
        for (i in l until r) {
            func(this[i], i)
        }
        return
    }
    val m = (l + r) / 2
    coroutineScope {
        launch { pFor(l, m, func) }
        launch { pFor(m, r, func) }
    }
}

class ParallelFilter(private val input: IntArray, private val lambda: (Int) -> Boolean) : RecursiveTask<IntArray>() {
    override fun compute(): IntArray {
        val flags = IntArray(input.size)
        PFor(flags) { _, i ->
            if (lambda(i)) {
                flags[i] = 1
            } else {
                flags[i] = 0
            }
        }.fork().join()

        val sums = Scan(flags).fork().join()

        val result = IntArray(sums.last())
        PFor(input) { v, i ->
            if (flags[i] == 1) {
                result[sums[i]] = v
            }
        }
        return result
    }
}


suspend fun IntArray.scan(): IntArray {
    val tree = IntArray(size * 4)
    val result = IntArray(size + 1)
    buildReduceTree(this, tree, 0, size, 1)
    propagateScan(this, result, tree, 0, size, 1, 0)
    return result
}

suspend fun buildReduceTree(input: IntArray, tree: IntArray, left: Int, right: Int, index: Int) {
    if (right - left < block) {
        for (i in left until right) {
            tree[index] += input[i]
        }
        return
    }
    val m = (left + right) / 2
    coroutineScope {
        launch { buildReduceTree(input, tree, left, m, index * 2) }
        launch { buildReduceTree(input, tree, m, right, index * 2 + 1) }
    }
    tree[index] += tree[index * 2] + tree[index * 2 + 1]
}

suspend fun propagateScan(
    input: IntArray,
    result: IntArray,
    tree: IntArray,
    left: Int,
    right: Int,
    index: Int,
    prefix: Int
) {
    if (right - left < block) {
        result[left + 1] = prefix + input[left]
        for (i in left + 1 until right) {
            result[i + 1] = input[i] + result[i]
        }
        return
    }
    val m = (left + right) / 2
    coroutineScope {
        launch { propagateScan(input, result, tree, left, m, index * 2, prefix) }
        launch { propagateScan(input, result, tree, m, right, index * 2 + 1, prefix + tree[index * 2]) }
    }
}

class Scan(private val input: IntArray) : RecursiveTask<IntArray>() {
    val tree = IntArray(input.size * 4)
    val result = IntArray(input.size + 1)

    inner class ScanUpAction(
        val left: Int,
        val right: Int,
        val index: Int,
    ) : RecursiveAction() {
        override fun compute() {
            if (right - left < block) {
                for (i in left until right) {
                    tree[index] += input[i]
                }
                return
            }
            val m = (left + right) / 2

            invokeAll(
                ScanUpAction(left, m, index * 2),
                ScanUpAction(m, right, index * 2 + 1)
            )
            tree[index] += tree[index * 2] + tree[index * 2 + 1]
        }

    }

    inner class ScanDownAction(
        val left: Int,
        val right: Int,
        val index: Int,
        val prefix: Int,
    ) : RecursiveAction() {
        override fun compute() {
            if (right - left < block) {
                result[left + 1] = prefix + input[left]
                for (i in left + 1 until right) {
                    result[i + 1] = input[i] + result[i]
                }
                return
            }
            val m = (left + right) / 2
            invokeAll(
                ScanDownAction(left, m, index * 2, prefix).fork(),
                ScanDownAction(m, right, index * 2 + 1, prefix + tree[index * 2])
            )
        }
    }

    override fun compute(): IntArray {
        ScanUpAction(0, input.size, 1).fork().join()
        ScanDownAction(0, input.size, 1, 0).fork().join()
        return result
    }
}


fun serialScan(input: IntArray, result: IntArray, l: Int = 0, r: Int = input.size, base: Int = 0) {
    var sum = base
    for (i in l until r) {
        sum += input[i]
        result[i] = sum
    }
}

fun IntArray.serialReduce(l: Int, r: Int): Int {
    var sum = 0
    for (i in l until r) {
        sum += get(i)
    }
    return sum
}