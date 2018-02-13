package com.guochen.graph

import org.apache.spark.graphx.{Edge, EdgeDirection, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.graphframes.GraphFrame

object GraphXViewTableExample extends App {

  val sessionBuilder = SparkSession
    .builder()
    .master("local[4]")
    .appName("Graph")

  val session = sessionBuilder.getOrCreate()
  val sqlContext = session.sqlContext
  val sc = session.sparkContext

  val g: Graph[DataSetNode, Relationship] = createGraph()

  val propagateViews = g.pregel[Set[String]](Set[String](), Int.MaxValue, EdgeDirection.Out)(
    (destId, destAttr, message) => {
      if (destAttr.kind == "T")
        DataSetNode(destAttr.name, destAttr.kind, message)
      else {
        DataSetNode(destAttr.name, destAttr.kind, message + destAttr.name)
      }
    },
    triplet => {
      if (triplet.dstAttr.kind == "T")
        Iterator.empty //No more recursion if reach a Table
      else if (triplet.attr.relationship == "PARSING_ERROR")
        Iterator.empty //No more recursion if relationship is PARSING_ERROR
      else
        Iterator((triplet.dstId, triplet.srcAttr.parents))
    },
    (set1, set2) => set1 ++ set2
  )

  propagateViews.vertices.collect.foreach(println)

  private def createGraph(): Graph[DataSetNode, Relationship] = {
    val v = sqlContext.createDataFrame(List(
      ("table1", "T"),
      ("table2", "T"),
      ("view1", "V"),
      ("view2", "V"),
      ("view", "V")
    )).toDF("id", "type")
    // Edge DataFrame
    val e = sqlContext.createDataFrame(List(
      ("view1", "table1", "DEPEND_ON"),
      ("view2", "table2", "DEPEND_ON"),
      ("view", "view1", "DEPEND_ON"),
      ("view", "table2", "DEPEND_ON"),
      ("view1", "table2", "PARSING_ERROR")
    )).toDF("src", "dst", "relationship")

    //This conversion involves an expensive operation to index and generate corresponding Long vertex IDs
    val g = GraphFrame(v, e).toGraphX

    val typedVertices: RDD[(VertexId, DataSetNode)] = g.vertices.map(x => (
      x._1,
      DataSetNode(x._2.getString(0), x._2.getString(1), Set("Should Be Overwritten"))
    ))

    val typedEdges: RDD[Edge[Relationship]] = g.edges.map(x => Edge(
      x.srcId,
      x.dstId,
      Relationship(x.attr.getString(0), x.attr.getString(1), x.attr.getString(2))
    ))

    Graph(typedVertices, typedEdges)
  }
}

case class DataSetNode(name: String, kind: String, parents: Set[String])

case class Relationship(srcDs: String, destDs: String, relationship: String)
