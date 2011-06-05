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

package com.google.gwt.sample.gwtdlx.server

import com.google.gwt.sample.gwtdlx.client.SudokuApi;
import com.google.gwt.sample.gwtdlx.solver.Sudoku;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

class SudokuApiImpl extends RemoteServiceServlet with SudokuApi {

  def solveSudoku(board : Array[Array[Int]]) : Array[Array[Int]] = {    
    var sudoku = new Sudoku(board)
    var solved = sudoku.solve()
        
    return solved
  }
}
