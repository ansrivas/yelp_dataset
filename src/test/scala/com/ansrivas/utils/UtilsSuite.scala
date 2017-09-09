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

import org.scalatest.{ Matchers, WordSpec }

class UtilsSuite extends WordSpec with Matchers {

  val dirPath   = getClass.getResource("/testData").getPath
  val filesList = Utils.listFiles(dirPath)
  "listFiles with json filter" must {
    "return abc and bcd.json files" in {

      assert(filesList.isDefined)
      assert(filesList.get.length == 2)
      assert(filesList.get.map(x => x.getName) == List("abc.json", "bcd.json"))
    }
  }

  "getJsonPath with filename filter" must {
    "return correct filename" in {

      val actualFile = Utils.getJsonPath(filesList.get, "bcd.json")
      assert(actualFile.isSuccess)
      assert(actualFile.get.contains("bcd.json"))

      val actualFile2 = Utils.getJsonPath(filesList.get, "abc.json")
      assert(actualFile2.isSuccess)
      assert(actualFile2.get.contains("abc.json"))
    }
  }
}
