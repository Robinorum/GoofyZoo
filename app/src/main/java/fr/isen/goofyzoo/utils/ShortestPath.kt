package fr.isen.goofyzoo.utils

import android.content.Context
import org.json.JSONObject
import org.json.JSONArray
import kotlin.math.*
import java.util.*
import kotlin.collections.HashMap

data class Node(val lon: Double, val lat: Double)
data class Edge(val target: Node, val distance: Double)

class ZooGraph(private val context: Context) {
    private val graph = HashMap<Node, MutableList<Edge>>()

    private fun haversine(a: Node, b: Node): Double {
        val R = 6371000.0
        val dLat = Math.toRadians(b.lat - a.lat)
        val dLon = Math.toRadians(b.lon - a.lon)
        val lat1 = Math.toRadians(a.lat)
        val lat2 = Math.toRadians(b.lat)
        val a_ = sin(dLat/2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon/2).pow(2)
        return R * 2 * atan2(sqrt(a_), sqrt(1 - a_))
    }

    fun loadGeoJsonFromAssets(filename: String = "ways.geojson") {
        val jsonStr = context.assets.open(filename).bufferedReader().use { it.readText() }
        val geoJson = JSONObject(jsonStr)
        val features = geoJson.getJSONArray("features")

        for (i in 0 until features.length()) {
            val feature = features.getJSONObject(i)
            val properties = feature.getJSONObject("properties")
            val highway = properties.optString("highway")

            if (highway !in listOf("footway", "steps", "service")) continue

            val coords = feature.getJSONObject("geometry").getJSONArray("coordinates")
            val weight = if (highway == "steps") 2.0 else 1.0

            for (j in 0 until coords.length() - 1) {
                val start = coords.getJSONArray(j)
                val end = coords.getJSONArray(j + 1)

                val nodeA = Node(start.getDouble(0), start.getDouble(1))
                val nodeB = Node(end.getDouble(0), end.getDouble(1))
                val dist = haversine(nodeA, nodeB) * weight

                graph.getOrPut(nodeA) { mutableListOf() }.add(Edge(nodeB, dist))
                graph.getOrPut(nodeB) { mutableListOf() }.add(Edge(nodeA, dist))
            }
        }
    }

    private fun nearestNode(from: Node): Node {
        return graph.keys.minByOrNull { haversine(it, from) } ?: from
    }

    fun shortestPath(start: Node, end: Node): List<Node> {
        val startNode = nearestNode(start)
        val endNode = nearestNode(end)
        val distances = HashMap<Node, Double>()
        val prev = HashMap<Node, Node?>()
        val pq = PriorityQueue(compareBy<Pair<Node, Double>> { it.second })

        for (node in graph.keys) distances[node] = Double.POSITIVE_INFINITY
        distances[startNode] = 0.0
        pq.add(Pair(startNode, 0.0))

        while (pq.isNotEmpty()) {
            val (current, _) = pq.poll()
            if (current == endNode) break

            for (edge in graph[current] ?: emptyList()) {
                val alt = distances[current]!! + edge.distance
                if (alt < distances[edge.target]!!) {
                    distances[edge.target] = alt
                    prev[edge.target] = current
                    pq.add(Pair(edge.target, alt))
                }
            }
        }

        val path = mutableListOf<Node>()
        var current: Node? = endNode
        while (current != null) {
            path.add(current)
            current = prev[current]
        }

        return path.reversed()
    }
}
