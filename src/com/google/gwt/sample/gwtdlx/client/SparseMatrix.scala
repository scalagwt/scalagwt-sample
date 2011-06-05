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

package com.google.gwt.sample.gwtdlx.solver

import scala.collection.mutable.Map
import com.google.gwt.sample.gwtdlx.client.Util

/**
  * The matrix from which we'll be picking out columns to solve the set-cover
  * problem, for DLX.
  */
class SparseMatrix (rows : List[List[Int]]) {

  val column_table = Map.empty[Int, Column]
  val node_table = Map.empty[(Int,Int), Node]
  var columns : List[Column] = List()
  val header = new ColumnHeader()

  initialize();

  def initialize() {
    val myrows = Util.make2DArray(rows)

    for (r <- Util.xrange(myrows.length)) {
      for (c <- Util.xrange(myrows(0).length)) {

        if (myrows(r)(c) == 1) {
          val one = new Node(r, c)
          node_table.put((r,c), one)
        }
      }
    }

    build_columns()
    link_columns()
    link_nodes()
  }

  /**
    * Put all the columns that this matrix has into self.columns. Note that a
    * column can be empty of nodes.
    */
  def build_columns() {
    val colindices = Util.xrange(rows(0).length)

    val makeColumn = (index:Int) => (new Column(index))
 
    /* these refer to fields */
    // how do you do maps and lambdas in Scala?
    columns = (colindices map makeColumn).toList ::: List(header)

    for (col <- columns) {
      column_table.put(col.getName, col)
    }
  }

  def link_columns() {
    var prev:Column = null
    var first:Column = null

    for (column <- columns) {
      if (first == null) {
        first = column
      } else if (prev != null) {
        column.left = prev
        prev.right = column
      }
      prev = column
    }

    // now prev points at the last column we touched.
    prev.right = first
    first.left = prev
  }

  /**
    * Link all the nodes in the matrix together.
    */
  def link_nodes() {
    link_nodes_in_rows()
    link_nodes_in_columns()
  }

  /**
    * For each row, make the circular linked-list of those nodes.
    */
  def link_nodes_in_rows() {
    val rowindices = occupied_rowindices()

    for (r <- rowindices) {
      var colindices = colindices_for(r)
      var prev:Node = null
      var first:Node = null
      var node:Node = null

      for (c <- colindices) {
        node = node_table((r,c))

        if (first == null) {
          first = node
        } else if (prev != null) {
          node.left = prev
          prev.right = node
        }
        prev = node
      }

      node.right = first
      first.left = node
    }
  }

  /**
    * For each column, make the circular linked-list of those nodes, with
    * column header objects in the loop.
    */
  def link_nodes_in_columns() {
    val colindices = occupied_colindices()

    var prev:Node = null
    var first:Node = null
    var node:Node = null

    for (c <- colindices) {
      var column = column_table(c)
      val rowindices = rowindices_for(c)
      column.size = rowindices.length

      prev = null
      first = null
      for (r <- rowindices) {
        node = node_table((r,c))
        node.column = column

        if (first == null) {
          first = node
        } else if (prev != null) {
          node.up = prev
          prev.down = node
        }
        prev = node
      }

      column.up = node
      node.down = column

      column.down = first
      first.up = column
    }
  }

  def occupied_indices(location:Int) = {
    val accessor = location match {
      case 1 => (tup:Tuple2[Int,Int]) => tup._1
      case 2 => (tup:Tuple2[Int,Int]) => tup._2
    }

    val keys = node_table.keys

    val getindex = (rc:(Int,Int)) => accessor(rc)
    val indices = keys map getindex

    val uniqueIndices = Util.uniq(indices.toList)

    (uniqueIndices).sortWith(_ < _)
  }

  def occupied_colindices() = occupied_indices(2)
  def occupied_rowindices() = occupied_indices(1)

  /**
    * Take a given row index and return the list of the columns with nodes
    * on that row.
    */
  def colindices_for(r : Int) = {
    val all_colindices = occupied_colindices()
    all_colindices filter (node_table.contains(r,_))
  }

  /**
    * Take a given column index and return the list of the rows with nodes on
    * that column.
    */
  def rowindices_for(c : Int) = {
    val all_rowindices = occupied_rowindices()
    all_rowindices filter (node_table.contains(_,c))
  }

  /**
    * Remove a column; we can put it back in later.
    */
  def cover(column : Column) {
    column.right.left = column.left
    column.left.right = column.right

    var rowstart = column.down
    while (rowstart != column) {
      var node = rowstart.right

      while (node != rowstart) {
        node.down.up = node.up
        node.up.down = node.down
        node.column.size -= 1

        node = node.right
      }

      rowstart = rowstart.down
    }
  }

// uncover(c)
//   for each i <- U[c], U[U[c]] ... while i != c
//     for each j <- L[i], L[L[i]] ... while j != i
//       increment S[C[j]]
//       set U[D[j]] to j, D[U[j]] <- j
//   set L[R[c]] <- c and R[L[c]] <- c

  /**
   * Put a column back in.
   */
  def uncover(column : Column) {
    var rowstart = column.up
    while (rowstart != column) {
      var node = rowstart.left

      while (node != rowstart) {
        node.column.size += 1
        node.down.up = node
        node.up.down = node
        node = node.left
      }
      rowstart = rowstart.up
    }
    column.right.left = column
    column.left.right = column
  }
}

class Node (ri:Int, ci:Int)  {
  var left = this
  var right = this
  var up = this
  var down = this
  var column : Column = null

  val rowindex = ri
  val colindex = ci

  override def toString():String = {
    "(node " + rowindex + ", " + colindex + ")";
  }
}

// It's slightly gross, extending Node like this...
class Column(name:Int) extends Node(-1,-1) {
  var size = 0

  up = this
  down = this
  left = this
  right = this

  def getName() = name

  override def toString() = {
    "(col " + name + " with nodes: " + listNodes + ")"
  }

  def listNodes() = {
    var toReverse = List[Node]();

    var here:Node = this.down

    while(here != this) {
      toReverse = here :: toReverse
      here = here.down
    }

    toReverse.reverse
  }
}

/**
  * There's some better way to express this; a Column Header isn't really a
  * specific type of Column. But it needs to sit in the same list.
  */
class ColumnHeader extends Column(-1)
