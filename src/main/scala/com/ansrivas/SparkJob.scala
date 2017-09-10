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

package com.ansrivas

import java.io.File

import com.ansrivas.utils.Utils
import com.recogizer.tsspark.utils.Logging
import org.apache.spark.sql.{ DataFrame, SparkSession }
import org.apache.spark.sql.functions._
import Context.sparkSession.sqlContext.implicits._

class SparkJob(sparkSession: SparkSession) extends Serializable with Logging {

  /**
    *
    * @param msg
    */
  def printHeader(msg: String) = {
    logger.info("=" * msg.length)
    logger.info(msg)
    logger.info("=" * msg.length)
  }

  /**
    *
    * @param businessDF
    * @param reviewssDF
    * @return
    */
  def getTop10UsersInterestedInHealth(businessDF: DataFrame, reviewssDF: DataFrame) =
    businessDF
      .select($"business_id", explode($"categories").as("categories_flattened"))
      .filter($"categories_flattened" === "Health & Medical")
      .join(reviewssDF, Seq("business_id"))
      .orderBy(reviewssDF("date").desc)
      .select(reviewssDF("user_id"), $"business_id")
      .limit(10)

  /**
    *
    * @param businessDF
    * @return
    */
  def getTop10BusinessesWithReviewCountsAndStars(businessDF: DataFrame): DataFrame =
    businessDF
      .select($"business_id", $"is_open", $"stars", $"review_count")
      .filter($"is_open" === "1")
      .orderBy(desc("review_count"), desc("stars"))
      .limit(10)

  /**
    *
    * @param usersDF
    * @return
    */
  def getTopYelpersWithMaxNumberOfFriends(usersDF: DataFrame): DataFrame =
    usersDF
      .select($"user_id", explode($"friends").as("friends_array"))
      .groupBy($"user_id")
      .agg(count("user_id").as("count"))
      .orderBy(desc("count"))

  /**
    *
    * @param sql
    * @return
    */
  def executeSql(sql: String): DataFrame =
    sparkSession.sql(sql)

  /**
    *
    * @param jsonFileName
    * @return
    */
  def loadDFAndPrintStats(jsonFileName: String): DataFrame = {
    printHeader("Now reading json file %s ".format(jsonFileName))
    val df = sparkSession.read.format("json").load(jsonFileName)
    df.printSchema()
    df
  }

  /**
    *
    * @param files
    */
  def runJob(files: List[File]): Unit = {

    //TODO: Better to create a broadcast variable here, if it needs to be on HDFS

    val usersJson: String    = Utils.getJsonPath(files, "user.json").get
    val businessJson: String = Utils.getJsonPath(files, "business.json").get
    val reviewJson: String   = Utils.getJsonPath(files, "review.json").get

    val usersDF = loadDFAndPrintStats(usersJson)
    usersDF.cache()
    usersDF.createOrReplaceTempView("users")

    //==============================================================================
    printHeader("Now showing top 10 oldest registered yelpers")
    val oldest10YelpersSQL =
      """SELECT user_id, yelping_since
        | FROM users
        | ORDER BY (yelping_since) ASC
        | LIMIT 10""".stripMargin

    executeSql(oldest10YelpersSQL).show(10, truncate = false)
    //==============================================================================

    //==============================================================================
    printHeader("Now showing top 10 yelpers with maximum number of friends")
    getTopYelpersWithMaxNumberOfFriends(usersDF).show(10, truncate = false)
    //==============================================================================

    // Cleanup users DF here.
    usersDF.unpersist()

    val businessDF = loadDFAndPrintStats(businessJson)
    businessDF.cache()
    businessDF.createOrReplaceTempView("business")
    //==============================================================================
    printHeader("List of cities which have the maximum number of business open")
    val citiesWithMaximumBusinessOpenSQL =
      """SELECT count(city) AS count, city
        | FROM business WHERE is_open = '1'
        | GROUP BY city
        | ORDER BY count(city) DESC
        | LIMIT 10""".stripMargin
    executeSql(citiesWithMaximumBusinessOpenSQL).show(10, truncate = false)
    //==============================================================================

    //==============================================================================
    printHeader("Businesses with high review count and ordered by number of stars")
    getTop10BusinessesWithReviewCountsAndStars(businessDF).show(10, truncate = false)
    //==============================================================================

    val reviewsDF = loadDFAndPrintStats(reviewJson)
    // reviewsDF.cache()
    reviewsDF.createOrReplaceTempView("reviews")

    //==============================================================================
    printHeader("Top 10 users interested in Health & Medicare reviews")
    getTop10UsersInterestedInHealth(businessDF, reviewsDF).show(truncate = false)
    //==============================================================================

    // Clean all the DF manually, can be done above if not needed.
    // reviewsDF.unpersist()
    businessDF.unpersist()

  }
}
