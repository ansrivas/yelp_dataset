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

import com.recogizer.tsspark.utils.Logging

class SparkJob extends Serializable with Logging {
  val sparkSession = Context.sparkSession

  def runJob(files: List[File]): Unit = {

    // Read here the users file first and generate some basic results for them.
    // Some map which gives you file path from a key

    val paths = Map(
      "users" -> "/mnt/4CECFA4BECFA2F38/Dropbox/local-workspace/ankur-projects/personal/yelp_dataset/dataset/user.json"
    )
    val usersDF = sparkSession.read.format("json").load(paths.get("users").get)
    usersDF.cache()

    usersDF.createOrReplaceTempView("users")

    val oldest10Yelpers = """SELECT user_id FROM users order by (yelping_since) limit 10"""
    sparkSession.sql(oldest10Yelpers).show(12, truncate = false)

  }
}
