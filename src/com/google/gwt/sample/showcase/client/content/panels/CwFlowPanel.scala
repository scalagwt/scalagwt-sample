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
package com.google.gwt.sample.showcase.client.content.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.sample.showcase.client.ContentWidget;
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData;
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource;
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

object CwFlowPanel {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwFlowPanelDescription(): String

    def cwFlowPanelItem(): String

    def cwFlowPanelName(): String
  }
}

/**
 * Example file.
 */
@ShowcaseStyle(Array(".cw-FlowPanel-checkBox"))
class CwFlowPanel(constants: CwFlowPanel.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwFlowPanelDescription

  override def getName() = constants.cwFlowPanelName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a Flow Panel
    val flowPanel = new FlowPanel();
    flowPanel.ensureDebugId("cwFlowPanel");

    // Add some content to the panel
    for (i <- 0 until 30) {
      val checkbox = new CheckBox(constants.cwFlowPanelItem + " " + i);
      checkbox.addStyleName("cw-FlowPanel-checkBox");
      flowPanel.add(checkbox);
    }

    // Return the content
    return flowPanel;
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) = {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }
}
