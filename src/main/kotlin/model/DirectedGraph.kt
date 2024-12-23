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
            neighborsBlock = { it.weightedEdgeNodes },
        )
    }

    fun findNode(value: T): Node<T> {
        return nodes.first { it.value == value }
    }

    private val Node<T>.edgeNodes: Set<Node<T>>
        get() = edgeValues.map { findNode(it.key) }.toSet()

    private val Node<T>.weightedEdgeNodes: Set<WeightedNode<Node<T>>>
        get() = edgeValues.mapKeys { (value, _) -> findNode(value) }
            .map { (node, distance) -> WeightedNode(node, distance) }
            .toSet()


    data class Node<T>(
        val value: T,
        val edgeValues: Map<T, Int>,
    ) {
        override fun toString(): String {
            return "DirectedGraph.Node(value=$value, edges=${edgeValues.map { it.key }})"
        }
    }

    fun findCliques(maxCliqueSize: Int = Int.MAX_VALUE): Collection<Set<Node<T>>> {
        return findCliques(
            clique = emptySet(),
            candidates = nodes.toSet(),
            disqualified = emptySet(),
            maxCliqueSize = maxCliqueSize,
        )
    }

    private fun findCliques(
        clique: Set<Node<T>>,
        candidates: Set<Node<T>>,
        disqualified: Set<Node<T>>,
        maxCliqueSize: Int,
    ): Collection<Set<Node<T>>> = buildList {
        if (clique.size > 1) {
            add(clique)
        }

        if (clique.size < maxCliqueSize && (candidates.isNotEmpty() || disqualified.isNotEmpty())) {
            val mutableCandidates = candidates.toMutableSet()
            val mutableDisqualified = disqualified.toMutableSet()
            for (candidate in candidates) {
                this += findCliques(
                    clique = clique + candidate,
                    candidates = mutableCandidates.intersect(candidate.edgeNodes),
                    disqualified = mutableDisqualified.intersect(candidate.edgeNodes),
                    maxCliqueSize = maxCliqueSize,
                )

                mutableCandidates -= candidate
                mutableDisqualified += candidate
            }
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
