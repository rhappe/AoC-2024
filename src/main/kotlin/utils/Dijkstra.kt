package utils

import java.util.PriorityQueue

data class DijkstraValue(
    val distance: Int,
    val steps: Int,
) : Comparable<DijkstraValue> {
    override fun compareTo(other: DijkstraValue): Int {
        return distance.compareTo(other.distance)
    }
}

data class DijkstraResult<T : Any>(
    val start: T,
    val values: Map<T, DijkstraValue>,
    val neighborsBlock: (T) -> Set<T>,
) {
    infix fun shortestPathTo(end: T): ArrayDeque<T>? {
        return getShortestPathTo(end, neighborsBlock)
    }

    fun getShortestPathTo(end: T, neighborsBlock: (T) -> Set<T>): ArrayDeque<T>? {
        var currentNode: T = end
        val pathNodes = mutableListOf<T>()

        while (currentNode != start) {
            val unfilteredNeighbors = neighborsBlock(currentNode).filter { it in values.keys }.toSet()
            val next = unfilteredNeighbors.minByOrNull { values.getValue(it) }
            if (next == null) return null
            pathNodes += currentNode
            currentNode = next
        }

        // finally add the start to the end of the list,
        // then reverse the paths since they were added in reverse order.
        pathNodes += start
        return ArrayDeque(pathNodes.reversed())
    }

    fun getAllShortestPathsTo(
        end: T,
        key: (T) -> Any = { it },
        neighborsBlock: (T) -> Set<T>,
    ): List<ArrayDeque<T>> {
        val paths = mutableListOf<List<T>>()
        val priorityQueue = ArrayDeque<Pair<T, List<T>>>()
        priorityQueue.add(end to emptyList())

        while (priorityQueue.isNotEmpty()) {
            val (node, list) = priorityQueue.removeFirst()
            var currentNode = node
            val newList = list.toMutableList()
            while (key(currentNode) != key(start)) {
                val neighbors = neighborsBlock(currentNode).filter { it in values.keys }.toSet()
                // can't do this. The value of the previous node is different depending on whether
                // a turn happened... But the previous node value + the weight of the jump should equal
                // the current node value.
                val minValue = neighbors.minOf { values.getValue(it).steps }
                val allMins = neighbors.filter { values.getValue(it).steps == minValue }

                // grab the "next" node and use it in this current iteration
                val next = allMins.first()
                // post all the remaining ones to the queue
                allMins.subList(1, allMins.size).forEach { remaining ->
                    priorityQueue.add(remaining to newList.toList())
                }

                newList += next
                currentNode = next
            }
            paths += newList
        }

        return paths.map { ArrayDeque(it) }
    }

}

typealias WeightedNode<T> = Pair<T, Int>

fun <T : Any> dijkstra(
    nodes: Collection<T>,
    start: T,
    neighborsBlock: (T) -> Collection<WeightedNode<T>>
): DijkstraResult<T> {
    val values = mutableMapOf<T, DijkstraValue>().withDefault { DijkstraValue(Int.MAX_VALUE, Int.MAX_VALUE) }
    val priorityQueue = PriorityQueue<Pair<DijkstraValue, WeightedNode<T>>>(compareBy { it.second.second })

    priorityQueue.add(DijkstraValue(0, 0) to (start to 0))
    values[start] = DijkstraValue(0, 0)

    while (priorityQueue.isNotEmpty()) {
        val (value, weightedNode) = priorityQueue.poll()
        val (node, currentDistance) = weightedNode
        val unfiltered = neighborsBlock(node)
        val neighborNodes = unfiltered.filter { (next, _) -> next in nodes }
        for ((next, weight) in neighborNodes) {
            val nextValues = DijkstraValue(
                distance = currentDistance + weight,
                steps = value.steps + 1,
            )
            if (nextValues.distance < values.getValue(next).distance) {
                values[next] = nextValues
                priorityQueue.add(nextValues to (next to nextValues.distance))
            }
        }
    }
    return DijkstraResult(
        start = start,
        values = values,
        neighborsBlock = { neighborsBlock(it).map { it.first }.toSet() },
    )
}
