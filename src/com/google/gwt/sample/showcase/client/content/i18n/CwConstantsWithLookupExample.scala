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
import com.google.gwt.event.dom.client.KeyUpEvent
import com.google.gwt.event.dom.client.KeyUpHandler
import com.google.gwt.event.logical.shared.SelectionEvent
import com.google.gwt.i18n.client.Constants
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.ShowcaseConstants
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseRaw
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.FlexTable
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.HorizontalPanel
import com.google.gwt.user.client.ui.TextBox
import com.google.gwt.user.client.ui.Widget

import java.util.MissingResourceException

object CwConstantsWithLookupExample {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwConstantsWithLookupExampleDescription(): String

    def cwConstantsWithLookupExampleLinkText(): String

    def cwConstantsWithLookupExampleMethodName(): String

    def cwConstantsWithLookupExampleName(): String

    def cwConstantsWithLookupExampleNoInput(): String

    def cwConstantsWithLookupExampleNoMatches(): String

    def cwConstantsWithLookupExampleResults(): String
  }
}

/**
 * Example file.
 */
@ShowcaseRaw(Array("ColorConstants.scala", "ColorConstants.properties"))
class CwConstantsWithLookupExample(constants: CwConstantsWithLookupExample.CwConstants) extends ContentWidget(constants) {

  /**
   * A {@link TextBox} where the user can select a color to lookup.
   */
  @ShowcaseData
  private var colorBox: TextBox = null

  /**
   * The {@link ColorConstants} that map colors to values.
   */
  private var colorConstants: ColorConstants = null

  /**
   * A {@link TextBox} where the results of the lookup are displayed.
   */
  @ShowcaseData
  private var colorResultsBox: TextBox = null

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

  override def getDescription() = constants.cwConstantsWithLookupExampleDescription

  override def getName() = constants.cwConstantsWithLookupExampleName

  override def hasStyle() = false

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create the internationalized constants
    colorConstants = GWT.create(classOf[ColorConstants])

    // Use a FlexTable to layout the content
    val layout = new FlexTable()
    val formatter = layout.getFlexCellFormatter()
    layout.setCellSpacing(5)

    // Add a link to the source code of the Interface
    val link = new HTML(" <a href=\"javascript:void(0);\">ColorConstants</a>")
    link.addClickHandler(new ClickHandler() {
      def onClick(event: ClickEvent) = selectTab(2)
    })
    val linkPanel = new HorizontalPanel()
    linkPanel.setSpacing(3)
    linkPanel.add(new HTML(constants.cwConstantsWithLookupExampleLinkText))
    linkPanel.add(link)
    layout.setWidget(0, 0, linkPanel)
    formatter.setColSpan(0, 0, 2)

    // Add a field so the user can type a color
    colorBox = new TextBox()
    colorBox.setText("red")
    colorBox.setWidth("17em")
    layout.setHTML(1, 0, constants.cwConstantsWithLookupExampleMethodName)
    layout.setWidget(1, 1, colorBox)

    // Show the last name
    colorResultsBox = new TextBox()
    colorResultsBox.setEnabled(false)
    colorResultsBox.setWidth("17em")
    layout.setHTML(2, 0, constants.cwConstantsWithLookupExampleResults)
    layout.setWidget(2, 1, colorResultsBox)

    // Add a handler to update the color as the user types a lookup value
    colorBox.addKeyUpHandler(new KeyUpHandler() {
      def onKeyUp(event: KeyUpEvent) = updateColor()
    })

    // Return the layout Widget
    updateColor()
    return layout
  }

  override def onInitializeComplete() {
    addConstantsTab()
  }

  override def onSelection(event: SelectionEvent[Integer]) {
    super.onSelection(event)

    val tabIndex = event.getSelectedItem.intValue
    if (!javaLoaded && tabIndex == 2) {
      // Load ErrorMessages.scala
      javaLoaded = true
      var className = classOf[ColorConstants].getName()
      className = className.substring(className.lastIndexOf(".") + 1)
      requestSourceContents(ShowcaseConstants.DST_SOURCE_RAW + className
          + ".scala.html", javaWidget, null)
    } else if (!propertiesLoaded && tabIndex == 3) {
      // Load ErrorMessages.properties
      propertiesLoaded = true
      var className = classOf[ColorConstants].getName()
      className = className.substring(className.lastIndexOf(".") + 1)
      requestSourceContents(ShowcaseConstants.DST_SOURCE_RAW + className
          + ".properties.html", propertiesWidget, null)
    }
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) = {
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
    add(javaWidget, "ColorConstants.scala")

    // Add a tab to show the properties
    propertiesWidget = new HTML()
    add(propertiesWidget, "ColorConstants.properties")
  }

  /**
   * Lookup the color based on the value the user entered.
   */
  @ShowcaseSource
  private def updateColor() {
    val key = colorBox.getText.trim
    if (key.equals("")) {
      colorResultsBox.setText(constants.cwConstantsWithLookupExampleNoInput)
    } else {
      try {
        val color = colorConstants.getString(key)
        colorResultsBox.setText(color)
      } catch {
        case e: MissingResourceException =>
          colorResultsBox.setText(constants.cwConstantsWithLookupExampleNoMatches)
      }
    }
  }
}
