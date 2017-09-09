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

import java.io.File

/**
  * Set of generic utility functions shared across the project
  */
object Utils {

  /**
    * Prints the usage of the jar
    */
  def printUsage() = println("Usage: java -jar main.jar <json files directory>")

  /**
    * jsonFiles represents a sorted list of files to be parsed as a part of export to tables.
    */
  val jsonFiles = List[String]("business.json",
                               "checkin.json",
                               "photos.json",
                               "review.json",
                               "tip.json",
                               "user.json").sorted

  /**
    * Gets a list of json files from a given path
    * @param dirPath Directory path to traverse
    * @param fileType A filter type to be applied to the
    * @return
    */
  def listFiles(dirPath: String, fileType: String = ".json"): Option[List[File]] = {

    val d = new File(dirPath)
    if (d.exists && d.isDirectory) {
      Some(
        d.listFiles
          .filter { f =>
            f.isFile && f.getName.endsWith(fileType)
          }
          .toList
          .sorted
      )
    } else {
      None
    }
  }
}
