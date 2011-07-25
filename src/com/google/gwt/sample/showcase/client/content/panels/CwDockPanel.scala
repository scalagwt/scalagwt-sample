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
package com.google.gwt.sample.showcase.client.content.panels

import com.google.gwt.core.client.GWT
import com.google.gwt.core.client.RunAsyncCallback
import com.google.gwt.i18n.client.Constants
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.DockPanel
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.HasHorizontalAlignment
import com.google.gwt.user.client.ui.ScrollPanel
import com.google.gwt.user.client.ui.Widget

object CwDockPanel {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwDockPanelCenter(): String

    def cwDockPanelDescription(): String

    def cwDockPanelEast(): String

    def cwDockPanelName(): String

    def cwDockPanelNorth1(): String

    def cwDockPanelNorth2(): String

    def cwDockPanelSouth1(): String

    def cwDockPanelSouth2(): String

    def cwDockPanelWest(): String
  }
}

/**
 * Example file.
 */
@ShowcaseStyle(Array(".cw-DockPanel"))
class CwDockPanel(constants: CwDockPanel.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwDockPanelDescription

  override def getName() = constants.cwDockPanelName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a Dock Panel
    val dock = new DockPanel()
    dock.setStyleName("cw-DockPanel")
    dock.setSpacing(4)
    dock.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER)

    // Add text all around
    dock.add(new HTML(constants.cwDockPanelNorth1), DockPanel.NORTH)
    dock.add(new HTML(constants.cwDockPanelSouth1), DockPanel.SOUTH)
    dock.add(new HTML(constants.cwDockPanelEast), DockPanel.EAST)
    dock.add(new HTML(constants.cwDockPanelWest), DockPanel.WEST)
    dock.add(new HTML(constants.cwDockPanelNorth2), DockPanel.NORTH)
    dock.add(new HTML(constants.cwDockPanelSouth2), DockPanel.SOUTH)

    // Add scrollable text in the center
    val contents = new HTML(constants.cwDockPanelCenter)
    val scroller = new ScrollPanel(contents)
    scroller.setSize("400px", "100px")
    dock.add(scroller, DockPanel.CENTER)

    // Return the content
    dock.ensureDebugId("cwDockPanel")
    return dock
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) = {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }
}
