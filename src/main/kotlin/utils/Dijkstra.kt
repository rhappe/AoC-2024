package utils

import java.util.PriorityQueue

data class DijkstraResult<T>(
    val start: T,
    val distances: Map<T, Int>,
    val neighborsBlock: (T) -> Set<T>,
) {
    infix fun shortestPathTo(end: T): ArrayDeque<T>? {
        return getShortestPathTo(end, neighborsBlock)
    }

    fun getShortestPathTo(end: T, neighborsBlock: (T) -> Set<T>): ArrayDeque<T>? {
        var currentNode: T = end
        val pathNodes = mutableListOf<T>()

        while (currentNode != start) {
            val unfilteredNeighbors = neighborsBlock(currentNode).filter { it in distances.keys }.toSet()
            val next = unfilteredNeighbors.minByOrNull { distances.getValue(it) }
            if (next == null) return null
            pathNodes += currentNode
            currentNode = next
        }
        // reverse the paths since they were added in reverse order.
        return ArrayDeque(pathNodes.reversed())
    }

    fun getAllShortestPathsTo(end: T, neighborsBlock: (T) -> Set<T>): List<ArrayDeque<T>> {
        val paths = mutableListOf<List<T>>()
        val priorityQueue = ArrayDeque<Pair<T, List<T>>>()
        priorityQueue.add(end to emptyList())

        while (priorityQueue.isNotEmpty()) {
            val (node, list) = priorityQueue.removeFirst()
            var currentNode = node
            val newList = list.toMutableList()
            while (currentNode != start) {
                val neighbors = neighborsBlock(currentNode).filter { it in distances.keys }.toSet()
                // can't do this. The value of the previous node is different depending on whether
                // a turn happened... But the previous node value + the weight of the jump should equal
                // the current node value.
                val minValue = neighbors.minOf { distances.getValue(it) }
                val allMins = neighbors.filter { distances.getValue(it) == minValue }

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

fun <T> dijkstra(nodes: Collection<T>, start: T, neighborsBlock: (T) -> Set<WeightedNode<T>>): DijkstraResult<T> {
    val distances = mutableMapOf<T, Int>().withDefault { Int.MAX_VALUE }
    val priorityQueue = PriorityQueue<WeightedNode<T>>(compareBy { it.second })

    priorityQueue.add(start to 0)
    distances[start] = 0

    while (priorityQueue.isNotEmpty()) {
        val (node, currentDistance) = priorityQueue.poll()
        val neighborNodes = neighborsBlock(node).filter { (next, _) -> next in nodes }
        for ((next, weight) in neighborNodes) {
            val nextDistance = currentDistance + weight
            if (nextDistance < distances.getValue(next)) {
                distances[next] = nextDistance
                priorityQueue.add(next to nextDistance)
            }
        }
    }
    return DijkstraResult(
        start = start,
        distances = distances,
        neighborsBlock = { neighborsBlock(it).map { it.first }.toSet() },
    )
}
