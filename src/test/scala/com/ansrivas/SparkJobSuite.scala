/*
 * Copyright (c) 2017 com.ansrivas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ansrivas.utils

import com.ansrivas.SparkJob
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.scalatest.{ BeforeAndAfterAll, FlatSpec, Matchers, WordSpec }

class SparkJobSuite extends WordSpec with Matchers with BeforeAndAfterAll with DataFrameSuiteBase {

  override def beforeAll(): Unit =
    super.beforeAll()
  // and then setup my data and stuff
  override def afterAll(): Unit =
    // teardown my own stuff
    super.afterAll()

  val dirPath              = getClass.getResource("/jsons").getPath
  val filesList            = Utils.listFiles(dirPath).get
  val usersJson: String    = Utils.getJsonPath(filesList, "user.json").get
  val businessJson: String = Utils.getJsonPath(filesList, "business.json").get
  lazy val usersDF         = spark.read.format("json").load(usersJson)
  lazy val businessDF      = spark.read.format("json").load(businessJson)
  lazy val sparkJob        = new SparkJob(spark)

  val oldest10YelpersSQL =
    """SELECT user_id, yelping_since
        | FROM users
        | ORDER BY (yelping_since) ASC
        | LIMIT 10""".stripMargin

  val citiesWithMaximumBusinessOpenSQL =
    """SELECT count(city) AS count, city
      | FROM business WHERE is_open = '1'
      | GROUP BY city
      | ORDER BY count(city) DESC
      | LIMIT 10""".stripMargin

  "getTop10OldestRegisteredYelpers" must {
    "return correct dataframe " in {
      import spark.sqlContext.implicits._

      usersDF.createOrReplaceTempView("users")

      val expectedDF = spark.sparkContext
        .parallelize(
          List[(String, String)](("om5ZiponkpRqUNa3pVPiRg", "2006-01-18"),
                                 ("g3V76Ja0XgWS1rqx0gxL_A", "2007-09-11"))
        )
        .toDF("user_id", "yelping_since")

      assertDataFrameEquals(expectedDF, sparkJob.executeSql(oldest10YelpersSQL).limit(2))
    }
  }

  "getTopYelpersWithMaxNumberOfFriends" must {
    "return correct dataframe" in {
      import spark.sqlContext.implicits._
      val expectedDF = spark.sparkContext
        .parallelize(
          List[(String, Long)](("om5ZiponkpRqUNa3pVPiRg", 5163), ("lsSiIjAKVl-QRxKjRErBeg", 1256))
        )
        .toDF("user_id", "count")
      assertDataFrameEquals(expectedDF,
                            sparkJob.getTopYelpersWithMaxNumberOfFriends(usersDF).limit(2))
    }
  }

  "getCitiesWithMaximumBusinessOpen" must {
    "return correct cities" in {
      import spark.sqlContext.implicits._
      businessDF.createOrReplaceTempView("business")

      val expectedDF = spark.sparkContext
        .parallelize(
          List[(Long, String)]((3, "Pittsburgh"), (1, "Scottsdale"), (1, "Gilbert"))
        )
        .toDF("count", "city")
      assertDataFrameEquals(expectedDF,
                            sparkJob.executeSql(citiesWithMaximumBusinessOpenSQL).limit(3))

    }
  }
}
