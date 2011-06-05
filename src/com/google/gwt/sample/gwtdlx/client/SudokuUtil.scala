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

object SudokuUtil {

  def buildBlankBoard() = {
    val out = new Array[Array[Int]](9)

    for (i <- (0 until 9)) {
      out(i) = new Array[Int](9)
    }

    out
  }

  /**
   * Take a sudoku puzzle (as a 9x9 nested list) and return a list-of-lists
   * representing the 729-tall, 324-wide matrix.
   */
  def squaresToDlxRows(squares:Array[Array[Int]]) = {
    var out:List[List[Int]] = List()

    for (rowIndex <- (0 until squares.length)) {
      for (colIndex <- (0 until squares(rowIndex).length)) {
        var here = squares(rowIndex)(colIndex)
        var rowsForHere = dlxRowsForSquare(rowIndex, colIndex, here)

        for (dlxRow <- rowsForHere) {
          out = (dlxRow :: out)
        }
      }
    }
    out
  }

  /**
   * Construct the dlx rows that correspond to a given square. If value is in
   * the range [1, 9], we only return one row. Otherwise, we'll have many
   * rows.
   */
  def dlxRowsForSquare(rowindex:Int, colindex:Int, value:Int) = {
    if ((1 <= value) && (value <= 9)) {
      List(dlxRow(rowindex, colindex, value))
    } else {
      (1 to 9).toList.map( (value => dlxRow(rowindex, colindex, value)))
    }
  }

  /** 
   * Build the row representing the idea that a given sudoku row/col has a
   * particular value. Will have length 324.
   * Constraints go in the order:
   *
   * row-column: 81 columns that have 0s, save a 1 for the row,col that this
   * possibility represents, at row*9 + col
   *
   * row-number: Each row only contains each number once. This section
   * represents the row that this number is in, and the value; it will be 81
   * elements long, and contain a single 1 in the position row*9 + (value-1).
   *
   * column-number: Each column only contains each number once. This section
   * represents the column  that this number is in, and the value. The 1 is in
   * position column*9 + (value-1).
   * 
   * box-number: Each box only contains each number once. The 1 in this section
   * is at position boxnum*9 + (value-1)
   *
   * For example, the idea that row/col (0,0) has #1 looks like: row0col0
   * row0value1 col0value1 box0value1.
   * 
   * Likewise, the possibility of #9 in (8,8), the lower-rightmost square, is:
   * row8col8 row8value9 col8value9 box8value9.
   */
  def dlxRow(rowIndex:Int, colIndex:Int, v:Int) = {
    val boxIndex = rowColToBox(rowIndex, colIndex)
    val value = v - 1

    encode(rowIndex,colIndex) :::
    encode(rowIndex,value) :::
    encode(colIndex,value) :::
    encode(boxIndex,value) 
  }

   /**
    * Pull (row,col,value) out from an encoded DLX list.
    */
  def dlxRowToRcv(dlxrow:List[Int]) = {
    val rowcol = dlxrow.take(81)
    val rownum = dlxrow.drop(81).take(81)

    val rowColTuple = decode(rowcol)
    val ignoreNumTuple = decode(rownum)

    val row = rowColTuple._1
    val col = rowColTuple._2
    val num = ignoreNumTuple._2

    (row,col,num + 1)
  }

  /**
   * Build a list of 81 values, with a 1 in the spot corresponding to the value
   * of the major attribute and minor attribute.
   */
  def encode(major:Int, minor:Int) = {
    val out = new Array[Int](81)

    val location = (major * 9 + minor)
    out(location) = 1
    out.toList
  }

  /**
   * Take a list of 81 values with a single 1, decode two values out of its
   * position. Return them in a tuple (major,minor).
   */
  def decode(lst:List[Int]):(Int,Int) = {
    val position:Int = index(lst, 1)
    val minor:Int = position % 9
    val major:Int = position / 9
    (major,minor)
  }

  /**
   * Return the location of the specified value in the list, if it's present,
   * or -1 to indicate that it's not.
   */
  def index[T](lst:List[T], value:T):Int = {
    for(i <- (0 until lst.length)) {
      if (lst(i) == value) {
        return i
      }
    }
    return -1
  }

  /**
   * Return the index for the box that the given (r, c) sudoku coordinates
   * fits into. Boxes go like this:
   * 0 1 2
   * 3 4 5
   * 6 7 8
   */
  def rowColToBox(row:Int, col:Int) = (row - (row % 3)) + (col / 3)
}
