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
package com.google.gwt.sample.gwtdlx.client

import com.google.gwt.dom.client.Document
import com.google.gwt.dom.client.InputElement
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.event.dom.client.ClickHandler
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui._
import com.google.gwt.core.client.{Scheduler, EntryPoint, GWT}
import com.google.gwt.core.client.Scheduler.ScheduledCommand

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
class GwtDlx extends EntryPoint {
   private val grid: Grid = new Grid(9, 9)
   private val ajax: Label = new Label("the browser is thinking really hard...")
   private val noSolution: HTML = new HTML("&empty;")

   def onModuleLoad: Unit = {
      var button: Button = new Button("solve")
      var clearButton: Button = new Button("clear")

      button addClickHandler onClick
      clearButton addClickHandler onClear

      RootPanel.get("board").add(grid)
      RootPanel.get("button").add(button)
      RootPanel.get("button").add(clearButton)

      noSolution.setVisible(false)
      noSolution.setStyleName("fail")

      RootPanel.get.add(noSolution)
      RootPanel.get.add(ajax)
      ajax.setStyleName("ajax")
      ajax.setVisible(false)
      ajax.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER)
      noSolution.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER)

      val board = Array(Array(" ", "2", " ",   "1", " ", "9",   "8", "7", " "),
                        Array(" ", "3", " ",   "5", "8", " ",   "2", "9", "6"),
                        Array("7", " ", "9",   " ", " ", " ",   "5", "4", " "),

                        Array(" ", "1", " ",   "2", "5", " ",   "4", " ", " "),
                        Array("3", " ", "2",   " ", " ", " ",   "6", " ", "7"),
                        Array(" ", " ", "4",   " ", "1", "3",   " ", "5", " "),

                        Array(" ", "9", "3",   " ", " ", " ",   "7", " ", "5"),
                        Array("5", "4", "6",   " ", "7", "1",   " ", "2", " "),
                        Array(" ", "7", "8",   "9", " ", "5",   " ", "6", " "))
      for (r <- 0 until 9) {
        for (c <- 0 until 9) {
          val tb = new TextBox()
          tb.setMaxLength(1)
          grid.setWidget(r, c, tb)
          getBox(r, c).setValue(board(r)(c))
        }
      }
   }

   private class FunctionClickHandler(f: => Unit) extends ClickHandler {
      def onClick(event: ClickEvent) = f;
   }

   implicit private def unit2clickHandler(f: => Unit): ClickHandler =
      new FunctionClickHandler(f)

   private def onClear: Unit = {
      noSolution.setVisible(false)
      for (r <- 0 to 8) {
         for (c <- 0 to 8) {
            getBox(r, c).setValue("")
         }
      }
   }

   private def onClick: Unit = {
      ajax.setVisible(true)
      val board = Array.tabulate(9,9) { (r: Int, c: Int) =>
         val elt = getBox(r,c)
         val str = elt.getValue
         if (str.length == 1 && Character.isDigit(str(0))) str(0) - '0'
         else 0
      }
     Scheduler.get().scheduleDeferred { () => solve(board) }
   }

   private def getBox(r: Int, c: Int): TextBox = grid.getWidget(r, c).asInstanceOf[TextBox]

   implicit private def fn2command(f: () => Unit): ScheduledCommand =
     new ScheduledCommand { def execute() = f() }

   private def solve(board: Array[Array[Int]]) = {
      val result = (new Sudoku(board)).solve
      ajax.setVisible(false)
      if (result == null) {
         noSolution.setVisible(true)
      } else {
         noSolution.setVisible(false)
         for (r <- 0 until result.length) {
            for (c <- 0 until result(r).length) {
               getBox(r, c).setValue(result(r)(c) + "")
            }
         }
         ajax.setVisible(false)
      }
   }
}

