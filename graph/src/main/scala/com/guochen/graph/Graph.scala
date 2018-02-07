package com.guochen.graph

import org.apache.spark.sql.SparkSession
import org.graphframes.GraphFrame

object Graph extends App {

  val sessionBuilder = SparkSession
    .builder()
    .master("local[4]")
    .appName("Graph")

  val session = sessionBuilder.getOrCreate()
  val sqlContext = session.sqlContext
  session.sparkContext.setCheckpointDir("/tmp/graph")

  // Vertex DataFrame
  val v = sqlContext.createDataFrame(List(
    ("a", "Alice", 34),
    ("b", "Bob", 36),
    ("c", "Charlie", 30),
    ("d", "David", 29),
    ("e", "Esther", 32),
    ("f", "Fanny", 36),
    ("g", "Gabby", 60)
  )).toDF("id", "name", "age")

  // Edge DataFrame
  val e = sqlContext.createDataFrame(List(
    ("a", "b", "friend"),
    ("b", "c", "follow"),
    ("c", "b", "follow"),
    ("f", "c", "follow"),
    ("e", "f", "follow"),
    ("e", "d", "friend"),
    ("d", "a", "friend"),
    ("a", "e", "friend")
  )).toDF("src", "dst", "relationship")
  // Create a GraphFrame
  val g = GraphFrame(v, e)

  // Example: Find based on relationship
  //  val chain4: DataFrame = g.find("(a)-[ab]->(b); (b)-[bc]->(c); (c)-[cd]->(d)")
  //  chain4.show()
  //
  //  private val friendCountCol: Column = Seq("ab", "bc", "cd").foldLeft(lit(0))((cnt, e) => {
  //    when(col(e)("relationship") === "friend", cnt + 1).otherwise(cnt)
  //  })
  //
  //  chain4.where(friendCountCol >= 2).show()


  //  Example: BFS to find the shortest path
  //  val paths: DataFrame = g.bfs.fromExpr("name = 'Esther'").toExpr("age < 32").run()
  //  paths.show()


  //  Example: Get Strongly ConnectedComponents
  //  g.stronglyConnectedComponents.maxIter(10).run().show()


  //  Run PageRank until convergence to tolerance "tol".
  //  val results = g.pageRank.resetProbability(0.15).tol(0.1).run()
  //  // Display resulting pageranks and final edge weights
  //  // Note that the displayed pagerank may be truncated, e.g., missing the E notation.
  //  // In Spark 1.5+, you can use show(truncate=false) to avoid truncation.
  //  results.vertices.select("id", "pagerank").show()
  //  results.edges.select("src", "dst", "weight").show()

  //  Run PageRank for a fixed number of iterations.
  //  val results2 = g.pageRank.resetProbability(0.15).maxIter(5).run()


  // Example: Shortest path
  // g.shortestPaths.landmarks(Seq("a", "d")).run().show

  // Example: Triangle Count
  g.triangleCount.run().show()
}
