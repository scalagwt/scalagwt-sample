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
import com.google.gwt.user.client.ui.HasVerticalAlignment
import com.google.gwt.user.client.ui.HorizontalPanel
import com.google.gwt.user.client.ui.TextBox
import com.google.gwt.user.client.ui.Widget

object CwMessagesExample {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwMessagesExampleArg0Label(): String

    def cwMessagesExampleArg1Label(): String

    def cwMessagesExampleArg2Label(): String

    def cwMessagesExampleDescription(): String

    def cwMessagesExampleFormattedLabel(): String

    def cwMessagesExampleLinkText(): String

    def cwMessagesExampleName(): String

    def cwMessagesExampleTemplateLabel(): String
  }
}

/**
 * Example file.
 */
@ShowcaseRaw(Array("ErrorMessages.scala", "ErrorMessages.properties"))
class CwMessagesExample(constants: CwMessagesExample.CwConstants) extends ContentWidget(constants) {

  /**
   * The {@link TextBox} where the user enters argument 0.
   */
  @ShowcaseData
  private var arg0Box: TextBox = null

  /**
   * The {@link TextBox} where the user enters argument 1.
   */
  @ShowcaseData
  private var arg1Box: TextBox = null

  /**
   * The {@link TextBox} where the user enters argument 2.
   */
  @ShowcaseData
  private var arg2Box: TextBox = null

  /**
   * The error messages used in this example.
   */
  @ShowcaseData
  private var errorMessages: ErrorMessages = null

  /**
   * The {@link HTML} used to display the message.
   */
  @ShowcaseData
  private var formattedMessage: HTML = null

  /**
   * Indicates whether or not we have loaded the {@link ErrorMessages} java
   * source yet.
   */
  private var javaLoaded = false

  /**
   * The widget used to display {@link ErrorMessages} java source.
   */
  private var javaWidget: HTML = null

  /**
   * Indicates whether or not we have loaded the {@link ErrorMessages}
   * properties source yet.
   */
  private var propertiesLoaded = false

  /**
   * The widget used to display {@link ErrorMessages} properties source.
   */
  private var propertiesWidget: HTML = null

  override def getDescription() = constants.cwMessagesExampleDescription

  override def getName() = constants.cwMessagesExampleName

  override def hasStyle()= false

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create the internationalized error messages
    errorMessages = GWT.create(classOf[ErrorMessages])

    // Use a FlexTable to layout the content
    val layout = new FlexTable()
    val formatter = layout.getFlexCellFormatter
    layout.setCellSpacing(5)

    // Add a link to the source code of the Interface
    val link = new HTML(" <a href=\"javascript:void(0);\">ErrorMessages</a>")
    link.addClickHandler(new ClickHandler() {
      def onClick(event: ClickEvent) = selectTab(2)
    })
    val linkPanel = new HorizontalPanel()
    linkPanel.setSpacing(3)
    linkPanel.add(new HTML(constants.cwMessagesExampleLinkText))
    linkPanel.add(link)
    layout.setWidget(0, 0, linkPanel)
    formatter.setColSpan(0, 0, 2)

    // Show the template for reference
    val template = errorMessages.permissionDenied("{0}", "{1}", "{2}")
    layout.setHTML(1, 0, constants.cwMessagesExampleTemplateLabel)
    layout.setHTML(1, 1, template)

    // Add argument 0
    arg0Box = new TextBox()
    arg0Box.setText("amelie")
    layout.setHTML(2, 0, constants.cwMessagesExampleArg0Label)
    layout.setWidget(2, 1, arg0Box)

    // Add argument 1
    arg1Box = new TextBox()
    arg1Box.setText("guest")
    layout.setHTML(3, 0, constants.cwMessagesExampleArg1Label)
    layout.setWidget(3, 1, arg1Box)

    // Add argument 2
    arg2Box = new TextBox()
    arg2Box.setText("/secure/blueprints.xml")
    layout.setHTML(4, 0, constants.cwMessagesExampleArg2Label)
    layout.setWidget(4, 1, arg2Box)

    // Add the formatted message
    formattedMessage = new HTML()
    layout.setHTML(5, 0, constants.cwMessagesExampleFormattedLabel)
    layout.setWidget(5, 1, formattedMessage)
    formatter.setVerticalAlignment(5, 0, HasVerticalAlignment.ALIGN_TOP)

    // Add handlers to all of the argument boxes
    val keyUpHandler = new KeyUpHandler() {
      def onKeyUp(event: KeyUpEvent) = updateMessage()
    }
    arg0Box.addKeyUpHandler(keyUpHandler)
    arg1Box.addKeyUpHandler(keyUpHandler)
    arg2Box.addKeyUpHandler(keyUpHandler)

    // Return the layout Widget
    updateMessage()
    return layout
  }

  override def onInitializeComplete() {
    addMessagesTab()
  }

  override def onSelection(event: SelectionEvent[Integer]) {
    super.onSelection(event)

    val tabIndex = event.getSelectedItem.intValue
    if (!javaLoaded && tabIndex == 2) {
      // Load ErrorMessages.scala
      javaLoaded = true
      var className = classOf[ErrorMessages].getName
      className = className.substring(className.lastIndexOf(".") + 1)
      requestSourceContents(ShowcaseConstants.DST_SOURCE_RAW + className
          + ".scala.html", javaWidget, null)
    } else if (!propertiesLoaded && tabIndex == 3) {
      // Load ErrorMessages.properties
      propertiesLoaded = true
      var className = classOf[ErrorMessages].getName
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
   * Add a tab to this example to show the {@link ErrorMessages} source code.
   */
  private def addMessagesTab() {
    // Add a tab to show the interface
    javaWidget = new HTML()
    add(javaWidget, "ErrorMessages.scala")

    // Add a tab to show the properties
    propertiesWidget = new HTML()
    add(propertiesWidget, "ErrorMessages.properties")
  }

  /**
   * Update the formatted message.
   */
  @ShowcaseSource
  private def updateMessage() {
    val arg0 = arg0Box.getText.trim
    val arg1 = arg1Box.getText.trim
    val arg2 = arg2Box.getText.trim
    formattedMessage.setText(errorMessages.permissionDenied(arg0, arg1, arg2))
  }
}
