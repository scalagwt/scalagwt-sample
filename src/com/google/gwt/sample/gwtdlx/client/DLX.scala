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

import collection.mutable._

class DLX(matrix : SparseMatrix) {
  val header = matrix.header

  // Subtract one for the header.
  val solution:Array[Node] = new Array[Node](matrix.columns.length - 1);
  
  /**
    * Pick the most-constrained column still linked in the matrix.
    */
  def choose_column() : Column = {
    var lowestsize = Integer.MAX_VALUE 

    var here = header.right
    var choice:Column = null

    while(here != header) {
      var asColumn:Column = here.asInstanceOf[Column]

      if (asColumn.size < lowestsize) {
        choice = asColumn
        lowestsize = asColumn.size
      }
      here = here.right
    }

    choice
  }

  /**
   * Find a list of rows (nodes in rows, at least) that satisfies the set cover
   * problem.
   */
  def search(k:Int):Array[Node] = {

    if(header.right == header) {
      return solution
    }

    var here:Node = null
    val tocover = choose_column()
    matrix.cover(tocover)

    var rowstart = tocover.down
    while (rowstart != tocover) {
      solution(k) = rowstart

      here = rowstart.right
      while (here != rowstart) {
        matrix.cover(here.column)
        here = here.right
      }
      val further = search(k+1)
      if(further != null) {
        return further
      }

      // Didn't win, backing up.
      rowstart = solution(k)

      here = rowstart.left
      while (here != rowstart) {
        matrix.uncover(here.column)
        here = here.left
      }
      rowstart = rowstart.down
    }

    matrix.uncover(tocover)
    return null
  }
}
