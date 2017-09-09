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

class SparkJob extends Serializable with Logging {

  def runJob(files: List[File]): Unit = {

    //TODO: Better to create a broadcast variable here, if it needs to be on HDFS

    val usersJson: String = Utils.getJsonPath(files, "user.json").get

    logger.info("Now processing with %s".format(usersJson))
    val usersDF = Context.sparkSession.read.format("json").load(usersJson)
    usersDF.cache()

    usersDF.createOrReplaceTempView("users")

    val oldest10Yelpers = """SELECT user_id FROM users order by (yelping_since) limit 10"""
    Context.sparkSession.sql(oldest10Yelpers).show(12, truncate = false)

  }
}
