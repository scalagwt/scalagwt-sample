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
import com.google.gwt.i18n.client.Constants.DefaultStringValue
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.DecoratorPanel
import com.google.gwt.user.client.ui.FlexTable
import com.google.gwt.user.client.ui.HasHorizontalAlignment
import com.google.gwt.user.client.ui.TextBox
import com.google.gwt.user.client.ui.Widget

object CwDecoratorPanel {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    @DefaultStringValue("Add rounded corners to any Widget using the Decorator Panel")
    def cwDecoratorPanelDescription(): String

    @DefaultStringValue("Description:")
    def cwDecoratorPanelFormDescription(): String

    @DefaultStringValue("Name:")
    def cwDecoratorPanelFormName(): String

    @DefaultStringValue("Enter Search Criteria")
    def cwDecoratorPanelFormTitle(): String

    @DefaultStringValue("Decorator Panel")
    def cwDecoratorPanelName(): String
  }
}

/**
 * Example file.
 */
@ShowcaseStyle(Array(
    ".gwt-DecoratorPanel", "html>body .gwt-DecoratorPanel",
    "* html .gwt-DecoratorPanel"))
class CwDecoratorPanel(constants: CwDecoratorPanel.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwDecoratorPanelDescription

  override def getName() = constants.cwDecoratorPanelName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a table to layout the form options
    val layout = new FlexTable()
    layout.setCellSpacing(6)
    val cellFormatter = layout.getFlexCellFormatter()

    // Add a title to the form
    layout.setHTML(0, 0, constants.cwDecoratorPanelFormTitle)
    cellFormatter.setColSpan(0, 0, 2)
    cellFormatter.setHorizontalAlignment(0, 0,
        HasHorizontalAlignment.ALIGN_CENTER)

    // Add some standard form options
    layout.setHTML(1, 0, constants.cwDecoratorPanelFormName)
    layout.setWidget(1, 1, new TextBox())
    layout.setHTML(2, 0, constants.cwDecoratorPanelFormDescription)
    layout.setWidget(2, 1, new TextBox())

    // Wrap the content in a DecoratorPanel
    val decPanel = new DecoratorPanel()
    decPanel.setWidget(layout)
    return decPanel
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }
}
