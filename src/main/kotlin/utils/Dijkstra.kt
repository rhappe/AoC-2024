package utils

import java.util.PriorityQueue

data class DijkstraResult<T>(
    val start: T,
    val distances: Map<T, Int>,
    val neighborsBlock: (T) -> Set<T>,
) {
    infix fun shortestPathTo(end: T): ArrayDeque<T>? {
        var currentNode: T = end
        val pathNodes = mutableListOf<T>()

        while (currentNode != start) {
            val next = neighborsBlock(currentNode).filter {
                it in distances.keys
            }.minByOrNull {
                distances.getValue(it)
            }
            if (next == null) return null
            pathNodes += currentNode
            currentNode = next
        }
        // reverse the paths since they were added in reverse order.
        return ArrayDeque(pathNodes.reversed())
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
