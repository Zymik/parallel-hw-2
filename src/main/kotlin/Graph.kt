interface Graph {
    val size: Int
    operator fun get(node: Int): IntArray
}


class CubeGraph(private val dimension: Int): Graph {

    override val size: Int = dimension * dimension * dimension

    override fun get(node: Int): IntArray {
        val index = getIndex(node)



        val (i, j, k) = index
        val size = booleanArrayOf(i != 0, j != 0, k != 0, i != dimension - 1, j != dimension - 1, k != dimension - 1).count { it }

        val result = IntArray(size)
        var pos = 0;
        if (i != 0) {
            result[pos] = getPos(i - 1, j, k)
            pos++
        }
        if (j != 0) {
            result[pos] = getPos(i, j - 1, k)
            pos++
        }
        if (k != 0) {
            result[pos] = getPos(i, j, k - 1)
            pos++
        }

        if (i != dimension - 1) {
            result[pos] = getPos(i + 1, j, k)
            pos++
        }
        if (j != dimension - 1) {
            result[pos] = getPos(i, j + 1, k)
            pos++
        }
        if (k != dimension - 1) {
            result[pos] = getPos(i, j, k + 1)
        }
        return result
    }

    private fun getIndex(node: Int): IntArray = intArrayOf(node % dimension, (node / dimension) % dimension, (node / dimension) / dimension)

    private fun getPos(i: Int, j: Int, k: Int): Int = i + j * dimension + k * dimension * dimension


}