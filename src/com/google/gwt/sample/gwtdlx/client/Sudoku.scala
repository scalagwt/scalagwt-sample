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
import Scheduled._

/**
 * Representation of a Sudoku puzzle, with methods to convert back and forth
 * from an instance of DLX.
 */
class Sudoku(squares : Array[Array[Int]]) {
  val dlxrows = SudokuUtil.squaresToDlxRows(squares)
  val sparseMatrix = new SparseMatrix(dlxrows) 
  val dlx = new DLX(sparseMatrix)

  /**
   * Solve the sudoku puzzle. Return value is a nested list in the same format
   * as the input to the constructor.
   */
  def solve(): Option[Array[Array[Int]]] @schedulable = {
    val dlxSoln = dlx.search(0)
    dlxSoln map { soln =>
       val nonNulls = soln.filter( (node => (node != null)) )
       val solnRows = nonNulls.map( (node => node.rowindex) )

       val dlxEncodedSoln = solnRows.map( (row => dlxrows(row)) )
       val rcvs = dlxEncodedSoln.map(SudokuUtil.dlxRowToRcv)

       val out = SudokuUtil.buildBlankBoard()

       for (rcv <- rcvs) {
          val r = rcv._1
          val c = rcv._2
          val v = rcv._3
          out(r)(c) = v
       }

       out
    }
  }
}
