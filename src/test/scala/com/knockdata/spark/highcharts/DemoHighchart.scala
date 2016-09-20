/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.knockdata.spark.highcharts

import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import org.junit.Test


import com.knockdata.spark.highcharts.model._

class DemoHighchart extends AbstractTestCase{

  val bank = DataSet.dfBank

  def debug(result: List[(List[String], Array[String], Array[Row])]): Unit = {
    val msg = result.map{case (keys, cols, rows) => keys.mkString(",") + "\n" + rows.mkString("\n")}.mkString("\n")
    super.debug(msg)
  }

  @Test
  def test1ColumnWithAgg(): Unit = {
    val chart: Highcharts = highcharts(
      DataSet.dfBank.series("name" -> "marital",
        "y" -> avg(col("balance"))))

    assertEqual("src/test/scala/com/knockdata/spark/highcharts/1ColumnWithAgg.json", chart)
  }

  @Test
  def testDrilldown1Level(): Unit = {
    val chart: Highcharts = highcharts(
      DataSet.dfBank
        .series("name" -> "marital",
            "y" -> avg(col("balance")))
        .drilldown(
          "name" -> "job",
            "y" -> avg(col("balance"))))

    assertEqual("src/test/scala/com/knockdata/spark/highcharts/Drilldown1Level.json", chart)
  }

  @Test
  def test2ColumnWithAgg(): Unit = {
    // example for HeatMap
    val chart: Highcharts = highcharts(
      DataSet.dfBank.series("x" -> "marital", "y" -> "job",
        "value" -> avg(col("balance"))))

    assertEqual("src/test/scala/com/knockdata/spark/highcharts/2ColumnWithAgg.json", chart)
  }

  @Test
  def testDrilldown2Level(): Unit = {
    // with 3 levels, the output is pretty big
    // number of data point is
    // size(marital) + size(marital) * size(balance) + size(marital) * size(balance) + size(education)
    val chart: Highcharts = highcharts(
      DataSet.dfBank.series(
        "name" -> "marital",
        "y" -> avg(col("balance")))
          .drilldown("name" -> "job",
        "y" -> avg(col("balance")))
          .drilldown("name" -> "education",
        "y" -> avg(col("balance"))))

    assertEqual("src/test/scala/com/knockdata/spark/highcharts/Drilldown2Level.json", chart)
  }

  @Test
  def test2ColumnDrilldown1Level(): Unit = {
    // there is no such Highchart which accept job field
    // it is only for a test to see drilldown works with 2 name column
    val chart: Highcharts = highcharts(
      DataSet.dfBank.series("name" -> "marital", "job" -> "job",
        "y" -> avg(col("balance"))).drilldown("name" -> "education",
        "y" -> avg(col("balance"))))

    assertEqual("src/test/scala/com/knockdata/spark/highcharts/2ColumnDrilldown1Level.json", chart)

  }

  @Test
  def testSeriesWithAgg(): Unit = {
    val chart: Highcharts = highcharts(DataSet.dfBank.seriesCol("marital").series("name" -> "job",
        "y" -> avg(col("balance"))))

    assertEqual("src/test/scala/com/knockdata/spark/highcharts/SeriesWithAgg.json", chart)
  }

  @Test
  def testSeriesDrilldown1Level(): Unit = {
    // with 3 levels, the output is pretty big
    // number of data point is
    // size(marital) + size(marital) * size(balance) + size(marital) * size(balance) + size(education)
    val chart: Highcharts = highcharts(DataSet.dfBank.seriesCol("marital").series("name" -> "job",
        "y" -> avg(col("balance"))).drilldown("name" -> "education",
        "y" -> avg(col("balance"))))

    assertEqual("src/test/scala/com/knockdata/spark/highcharts/SeriesDrilldown1Level.json", chart)
  }
}
