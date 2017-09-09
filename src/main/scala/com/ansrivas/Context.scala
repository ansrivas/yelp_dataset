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

import org.apache.spark.sql.SparkSession

/**
  * Context object has bunch of configurations related to spark context
  */
object Context {
  val envConfig = Environment()

  //================== Load the configurations here ==========================
  val appName = envConfig.getString("spark.appName")
  val master  = envConfig.getString("spark.master")

  //==========================================================================
  lazy val sparkSession =
    SparkSession.builder
      .master(master)
      .appName(appName)
      .getOrCreate()

  //===== If the output is expected to be cassandra, use these configs========
  //      .config("spark.cassandra.connection.host", cassandraHost)
  //      .config("spark.cassandra.auth.username",   cassandraUser)
  //      .config("spark.cassandra.auth.password",   cassandraPass)

  /**
    * stopSparkSession stops the underlying spark session
    */
  def stopSparkSession() = sparkSession.stop()
}
