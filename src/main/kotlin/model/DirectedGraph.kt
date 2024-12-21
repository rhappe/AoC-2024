package model

import utils.DijkstraResult
import utils.dijkstra
import utils.WeightedNode

data class DirectedGraph<T>(
    val nodes: Collection<Node<T>>,
) {
    fun findShortestPath(start: T, end: T): List<Pair<T, Int>> {
        val result = dijkstra(start) ?: error("Could not complete distance calculation from $start.")
        val path = result.shortestPathTo(findNode(end)) ?: error("Could not find a path from $start to $end.")
        return path.map { it.value to result.distances.getValue(it) }
    }

    fun dijkstra(start: T): DijkstraResult<Node<T>>? {
        val startNode = nodes.find { it.value == start } ?: return null
        return dijkstra(
            nodes = nodes,
            start = startNode,
            neighborsBlock = { node ->
                with(node) {
                    edgeNodes.map { (node, distance) -> WeightedNode(node, distance) }
                }
            }
        )
    }

    fun findNode(value: T): Node<T> {
        return nodes.first { it.value == value }
    }

    data class Node<T>(
        val value: T,
        val edgeValues: Map<T, Int>,
    ) {
        val DirectedGraph<T>.edgeNodes: Map<Node<T>, Int>
            get() = edgeValues.mapKeys { (value, _) -> findNode(value) }

        override fun toString(): String {
            return "DirectedGraph.Node(value=$value, edges=${edgeValues.map { it.key }})"
        }
    }
}

fun <T> DirectedGraph(
    values: Collection<T>,
    neighborsBlock: (T) -> Collection<T>,
    distanceBlock: (T, T) -> Int = { _, _ -> 1 },
): DirectedGraph<T> {
    val nodesMap = buildMap {
        for (value in values) {
            val currentNode = this[value] ?: DirectedGraph.Node(value, emptyMap())
            val neighbors = neighborsBlock(value)
                .filter { it in values }
                .associateWith { distanceBlock(value, it) }

            this[value] = currentNode.copy(edgeValues = neighbors)
        }
    }

    return DirectedGraph(nodesMap.values.toList())
}
