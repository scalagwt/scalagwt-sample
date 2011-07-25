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
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.Button
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget

object CwVerticalPanel {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwVerticalPanelButton(): String

    def cwVerticalPanelDescription(): String

    def cwVerticalPanelName(): String
  }
}

/**
 * Example file.
 */
class CwVerticalPanel(constants: CwVerticalPanel.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwVerticalPanelDescription

  override def getName() = constants.cwVerticalPanelName

  override def hasStyle() = false

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a Vertical Panel
    val vPanel = new VerticalPanel()
    vPanel.setSpacing(5)

    // Add some content to the panel
    for (i <- 1 until 10) {
      vPanel.add(new Button(constants.cwVerticalPanelButton + " " + i))
    }

    // Return the content
    vPanel.ensureDebugId("cwVerticalPanel")
    return vPanel
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) = {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }
}
