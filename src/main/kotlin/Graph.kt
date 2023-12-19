class Graph(val graphList: Array<IntArray>) {

    fun iterateOverNeighbours(id: Int, action: (Int) -> Unit) {
        for (i in graphList[id]) {
            action(i)
        }
    }

}