package com.google.gwt.sample.gwtdlx.client

/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import scala.collection.mutable.Set

object Util {
  def xrange(n:Int) = new Range(0,n,1)

  /**
   * Get the unique members of a list.
   */
  def uniq[T](lst:List[T]) = {
    val set = Set[T]()
    for (elt <- lst) {
      set += elt
    }

    set.toList
  }

  def make2DArray(lst:List[List[Int]]) = {
    val out = Array.ofDim[Int](lst.length, lst(0).length)
    var rowindex = 0
    var colindex = 0

    for (row <- lst) {
      colindex = 0
      for (elt <- row) {
        out(rowindex)(colindex) = elt
        colindex += 1
      }
      rowindex += 1
    }

    out
  }
}
