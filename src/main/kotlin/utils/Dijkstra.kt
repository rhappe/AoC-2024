package utils

import java.util.PriorityQueue

data class DijkstraResult<T>(
    val start: T,
    val distances: Map<T, Int>,
    val neighborsBlock: (T) -> Set<T>,
) {
    infix fun shortestPathTo(end: T): ArrayDeque<T> {
        val distances = distances.withDefault { Int.MAX_VALUE }
        var currentNode = end
        val pathNodes = buildList {
            do {
                add(currentNode)
                currentNode = neighborsBlock(currentNode).minBy { distances.getValue(it) }
            } while (currentNode != start)
        }
        // reverse the paths since they were added in reverse order,
        // and add the end node to the list since it wasn't added in iteration.
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
