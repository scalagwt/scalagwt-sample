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

import com.google.gwt.core.client.EntryPoint
import com.google.gwt.core.client.GWT
import com.google.gwt.dom.client.Document
import com.google.gwt.dom.client.InputElement
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.event.dom.client.ClickHandler
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui._

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
class GwtDlx extends EntryPoint {
   private val ajax: Label = new Label("ajax: the cloud is thinking.")
   private val api: SudokuApiAsync = GWT.create(classOf[SudokuApi])
   private val noSolution: HTML = new HTML("&empty;")

   def onModuleLoad: Unit = {
      var button: Button = new Button("solve")
      var clearButton: Button = new Button("clear")

      button addClickHandler onClick
      clearButton addClickHandler onClear

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
      api.solveSudoku(board, new Callback)
   }

   private def getBox(r: Int, c: Int): InputElement = {
      val cellname = "cell" + r + c
      Document.get.getElementById(cellname).asInstanceOf[InputElement]
   }

   private class Callback extends AsyncCallback[Array[Array[Int]]] {
      def onFailure(caught: Throwable) = {}
      def onSuccess(result: Array[Array[Int]]) = {
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
}
