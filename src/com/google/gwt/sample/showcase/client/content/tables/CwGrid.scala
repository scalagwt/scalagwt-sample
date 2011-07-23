/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.sample.showcase.client.content.tables

import com.google.gwt.core.client.GWT
import com.google.gwt.core.client.RunAsyncCallback
import com.google.gwt.i18n.client.Constants
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Showcase
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.Grid
import com.google.gwt.user.client.ui.Image
import com.google.gwt.user.client.ui.Widget

object CwGrid {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwGridDescription(): String

    def cwGridName(): String
  }
}

/**
 * Example file.
 */
class CwGrid(constants: CwGrid.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwGridDescription

  override def getName() = constants.cwGridName

  override def hasStyle() = false

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a grid
    val grid = new Grid(4, 4)

    // Add images to the grid
    val numRows = grid.getRowCount()
    val numColumns = grid.getColumnCount()
    for (row <- 0 until numRows) {
      for (col <- 0 until numColumns) {
        grid.setWidget(row, col, new Image(Showcase.images.gwtLogo()))
      }
    }

    // Return the panel
    grid.ensureDebugId("cwGrid")
    return grid
  }

  override def asyncOnInitialize(callback:AsyncCallback[Widget]) {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }
}
