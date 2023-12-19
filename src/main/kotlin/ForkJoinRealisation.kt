import java.util.concurrent.ForkJoinTask
import java.util.concurrent.RecursiveTask
import kotlin.math.min


var block = 16384

class PFor(private val array: IntArray, private val l: Int = 0, private val r: Int = array.size, val func: (Int, Int) -> Unit) :
    RecursiveTask<Unit>() {
    override fun compute() {
        if (r - l <= block) {
            for (i in l until r) {
                func(array[i], i)
            }
            return
        }
        val m = (l + r) / 2
        ForkJoinTask.invokeAll(
            PFor(array, l, m, func),
            PFor(array, m, r, func)
        )
    }
}

class Scan(private val input: IntArray) : RecursiveTask<IntArray>() {
    override fun compute(): IntArray {
        val result = IntArray(input.size)
        if (input.size < block) {
            serialScan(input, result)
            return result
        }

        var s = input.size / block
        if (input.size % block != 0) {
            s++
        }

        var sums = IntArray(s)

        PFor(sums) { _, i ->
            val left = block * i
            val right = min(block * (i + 1), input.size)
            sums[i] = input.serialReduce(left, right)
        }.fork().join()

        sums = Scan(sums).fork().join()
        PFor(sums) { _, i ->
            var j = i + 1
            var base = sums[i]
            if (i == sums.size - 1) {
                j = 0
                base = 0
            }

            val left = block * j
            val right = min(block * (j + 1), input.size)
            serialScan(input, result, left, right, base)
        }.fork().join()
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