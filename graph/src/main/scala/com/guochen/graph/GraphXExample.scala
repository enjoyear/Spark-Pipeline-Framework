package com.guochen.graph

import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.graphx.{Graph, VertexId}
import org.apache.spark.sql.SparkSession

object GraphXExample extends App {

  val sessionBuilder = SparkSession
    .builder()
    .master("local[4]")
    .appName("Graph")

  val session = sessionBuilder.getOrCreate()
  val sqlContext = session.sqlContext
  session.sparkContext.setCheckpointDir("/tmp/graph")

  // A graph with edge attributes containing distances
  val graph: Graph[Long, Double] =
    GraphGenerators.logNormalGraph(session.sparkContext, numVertices = 100).mapEdges(e => e.attr.toDouble)

  //===========  EXAMPLE  ===========
  //single source shortest path
  //===========  EXAMPLE  ===========

  val sourceId: VertexId = 42 // The ultimate source
  // Initialize the graph such that all vertices except the root have distance infinity.
  val initialGraph = graph.mapVertices(
    (id, _) => {
      if (id == sourceId)
        0.0
      else
        Double.PositiveInfinity
    }
  )

  val sssp = initialGraph.pregel(Double.PositiveInfinity)(
    (destId, destAttr, message) =>
      math.min(destAttr, message), // Vertex Program

    triplet => { // Send Message
      if (triplet.srcAttr + triplet.attr < triplet.dstAttr) {
        Iterator((triplet.dstId, triplet.srcAttr + triplet.attr))
      } else {
        Iterator.empty
      }
    },
    (a, b) => math.min(a, b) // Merge Message
  )
  println(sssp.vertices.collect.mkString("\n"))
}
