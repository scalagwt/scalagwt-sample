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
import com.google.gwt.user.client.ui.DecoratorPanel
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.HorizontalSplitPanel
import com.google.gwt.user.client.ui.Widget

object CwHorizontalSplitPanel {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwHorizontalSplitPanelDescription(): String

    def cwHorizontalSplitPanelName(): String

    def cwHorizontalSplitPanelText(): String
  }
}

/**
 * Example file.
 */
@ShowcaseStyle(Array(".gwt-HorizontalSplitPanel"))
class CwHorizontalSplitPanel(constants: CwHorizontalSplitPanel.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwHorizontalSplitPanelDescription

  override def getName() = constants.cwHorizontalSplitPanelName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a Horizontal Split Panel
    val hSplit = new HorizontalSplitPanel()
    hSplit.ensureDebugId("cwHorizontalSplitPanel")
    hSplit.setSize("500px", "350px")
    hSplit.setSplitPosition("30%")

    // Add some content
    var randomText = constants.cwHorizontalSplitPanelText
    for (i <- 0 until 2) {
      randomText += randomText
    }
    hSplit.setRightWidget(new HTML(randomText))
    hSplit.setLeftWidget(new HTML(randomText))

    // Wrap the split panel in a decorator panel
    val decPanel = new DecoratorPanel()
    decPanel.setWidget(hSplit)

    // Return the content
    return decPanel
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) = {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }
}
