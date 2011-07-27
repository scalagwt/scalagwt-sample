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
package com.google.gwt.sample.showcase.client.content.i18n

import com.google.gwt.core.client.GWT
import com.google.gwt.core.client.RunAsyncCallback
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.event.dom.client.ClickHandler
import com.google.gwt.event.logical.shared.SelectionEvent
import com.google.gwt.i18n.client.Constants
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Handlers._
import com.google.gwt.sample.showcase.client.JavaConversions._
import com.google.gwt.sample.showcase.client.ShowcaseConstants
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseRaw
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.FlexTable
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.HorizontalPanel
import com.google.gwt.user.client.ui.ListBox
import com.google.gwt.user.client.ui.TextBox
import com.google.gwt.user.client.ui.Widget

import java.util.Map

object CwConstantsExample {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwConstantsExampleDescription(): String

    def cwConstantsExampleLinkText(): String

    def cwConstantsExampleName(): String
  }
}

/**
 * Example file.
 */
@ShowcaseRaw(Array("ExampleConstants.scala", "ExampleConstants.properties"))
class CwConstantsExample(constants: CwConstantsExample.CwConstants) extends ContentWidget(constants) {

  /**
   * Indicates whether or not we have loaded the {@link ExampleConstants} java
   * source yet.
   */
  private var javaLoaded = false

  /**
   * The widget used to display {@link ExampleConstants} java source.
   */
  private var javaWidget: HTML = null

  /**
   * Indicates whether or not we have loaded the {@link ExampleConstants}
   * properties source yet.
   */
  private var propertiesLoaded = false

  /**
   * The widget used to display {@link ExampleConstants} properties source.
   */
  private var propertiesWidget: HTML = null

  override def getDescription() = constants.cwConstantsExampleDescription

  override def getName() = constants.cwConstantsExampleName

  override def hasStyle() = false

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create the internationalized constants
    val exampleConstants = GWT.create[ExampleConstants](classOf[ExampleConstants])

    // Use a FlexTable to layout the content
    val layout = new FlexTable()
    layout.setCellSpacing(5)

    // Add a link to the source code of the Interface
    val link = new HTML(
        " <a href=\"javascript:void(0);\">ExampleConstants</a>")
    link onClick { _ => selectTab(2) }
    val linkPanel = new HorizontalPanel()
    linkPanel.setSpacing(3)
    linkPanel.add(new HTML(constants.cwConstantsExampleLinkText()))
    linkPanel.add(link)
    layout.setWidget(0, 0, linkPanel)
    layout.getFlexCellFormatter().setColSpan(0, 0, 2)

    // Show the first name
    val firstNameBox = new TextBox()
    firstNameBox.setText("Amelie")
    firstNameBox.setWidth("17em")
    layout.setHTML(1, 0, exampleConstants.firstName())
    layout.setWidget(1, 1, firstNameBox)

    // Show the last name
    val lastNameBox = new TextBox()
    lastNameBox.setText("Crutcher")
    lastNameBox.setWidth("17em")
    layout.setHTML(2, 0, exampleConstants.lastName())
    layout.setWidget(2, 1, lastNameBox)

    // Create a list box of favorite colors
    val colorBox = new ListBox()
    val colorMap = exampleConstants.colorMap()
    for (entry <- colorMap.entrySet()) {
      val key = entry.getKey()
      val value = entry.getValue()
      colorBox.addItem(value, key)
    }
    layout.setHTML(3, 0, exampleConstants.favoriteColor())
    layout.setWidget(3, 1, colorBox)

    // Return the layout Widget
    return layout
  }

  override def onInitializeComplete() {
    addConstantsTab()
  }

  override def onSelection(event: SelectionEvent[Integer]) {
    super.onSelection(event)

    val tabIndex = event.getSelectedItem().intValue()
    if (!javaLoaded && tabIndex == 2) {
      // Load ErrorMessages.scala
      javaLoaded = true
      var className = classOf[ExampleConstants].getName()
      className = className.substring(className.lastIndexOf(".") + 1)
      requestSourceContents(ShowcaseConstants.DST_SOURCE_RAW + className
          + ".scala.html", javaWidget, null)
    } else if (!propertiesLoaded && tabIndex == 3) {
      // Load ErrorMessages.properties
      propertiesLoaded = true
      var className = classOf[ExampleConstants].getName()
      className = className.substring(className.lastIndexOf(".") + 1)
      requestSourceContents(ShowcaseConstants.DST_SOURCE_RAW + className
          + ".properties.html", propertiesWidget, null)
    }
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }

  /**
   * Add a tab to this example to show the messages interface.
   */
  private def addConstantsTab() {
    // Add a tab to show the interface
    javaWidget = new HTML()
    add(javaWidget, "ExampleConstants.scala")

    // Add a tab to show the properties
    propertiesWidget = new HTML()
    add(propertiesWidget, "ExampleConstants.properties")
  }
}
