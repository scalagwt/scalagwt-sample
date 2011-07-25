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
import com.google.gwt.user.client.ui.Label
import com.google.gwt.user.client.ui.TextBox
import com.google.gwt.user.client.ui.Widget

object CwPluralFormsExample {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwPluralFormsExampleArg0Label(): String

    def cwPluralFormsExampleDescription(): String

    def cwPluralFormsExampleFormattedLabel(): String

    def cwPluralFormsExampleLinkText(): String

    def cwPluralFormsExampleName(): String
  }
}

/**
 * Example file.
 */
@ShowcaseRaw(Array("PluralMessages.java", "PluralMessages_fr.properties"))
class CwPluralFormsExample(constants: CwPluralFormsExample.CwConstants) extends ContentWidget(constants) {

  /**
   * The {@link TextBox} where the user enters argument 0.
   */
  @ShowcaseData
  private var arg0Box: TextBox = null

  /**
   * The plural messages used in this example.
   */
  @ShowcaseData
  private var pluralMessages: PluralMessages = null

  /**
   * The {@link Label} used to display the message.
   */
  @ShowcaseData
  private var formattedMessage: Label = null

  /**
   * Indicates whether or not we have loaded the {@link PluralMessages} java
   * source yet.
   */
  private var javaLoaded = false

  /**
   * The widget used to display {@link PluralMessages} java source.
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

  override def getDescription() = constants.cwPluralFormsExampleDescription

  override def getName() = constants.cwPluralFormsExampleName

  override def hasStyle() = false

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create the internationalized error messages
    pluralMessages = GWT.create(classOf[PluralMessages])

    // Use a FlexTable to layout the content
    val layout = new FlexTable()
    val formatter = layout.getFlexCellFormatter()
    layout.setCellSpacing(5)

    // Add a link to the source code of the Interface
    val link = new HTML(" <a href=\"javascript:void(0);\">PluralMessages</a>");
    link.addClickHandler(new ClickHandler() {
      def onClick(event: ClickEvent) = selectTab(2)
    })
    val linkPanel = new HorizontalPanel()
    linkPanel.setSpacing(3)
    linkPanel.add(new HTML(constants.cwPluralFormsExampleLinkText))
    linkPanel.add(link)
    layout.setWidget(0, 0, linkPanel)
    formatter.setColSpan(0, 0, 2)

    // Add argument 0
    arg0Box = new TextBox()
    arg0Box.setText("13")
    layout.setHTML(2, 0, constants.cwPluralFormsExampleArg0Label)
    layout.setWidget(2, 1, arg0Box)

    // Add the formatted message
    formattedMessage = new Label()
    layout.setHTML(5, 0, constants.cwPluralFormsExampleFormattedLabel)
    layout.setWidget(5, 1, formattedMessage)
    formatter.setVerticalAlignment(5, 0, HasVerticalAlignment.ALIGN_TOP)

    // Add handlers to all of the argument boxes
    val keyUpHandler = new KeyUpHandler() {
      def onKeyUp(event: KeyUpEvent) = updateMessage()
    }
    arg0Box.addKeyUpHandler(keyUpHandler)

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
      // Load PluralMessages.java
      javaLoaded = true
      var className = classOf[PluralMessages].getName
      className = className.substring(className.lastIndexOf(".") + 1)
      requestSourceContents(ShowcaseConstants.DST_SOURCE_RAW + className
          + ".java.html", javaWidget, null)
    } else if (!propertiesLoaded && tabIndex == 3) {
      // Load ErrorMessages.properties
      propertiesLoaded = true
      var className = classOf[PluralMessages].getName()
      className = className.substring(className.lastIndexOf(".") + 1)
      requestSourceContents(ShowcaseConstants.DST_SOURCE_RAW + className
          + "_fr.properties.html", propertiesWidget, null)
    }
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) = {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }

  /**
   * Add a tab to this example to show the {@link PluralMessages} source code.
   */
  private def addMessagesTab() {
    // Add a tab to show the interface
    javaWidget = new HTML()
    add(javaWidget, "PluralMessages.java")

    // Add a tab to show the properties
    propertiesWidget = new HTML()
    add(propertiesWidget, "PluralMessages_fr.properties")
  }

  /**
   * Update the formatted message.
   */
  @ShowcaseSource
  private def updateMessage() {
    try {
      val count = Integer.parseInt(arg0Box.getText.trim)
      formattedMessage.setText(pluralMessages.treeCount(count))
    } catch {
      case e: NumberFormatException => // Ignore.
    }
  }

}
