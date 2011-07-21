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
package com.google.gwt.sample.showcase.client.content.text

import com.google.gwt.core.client.GWT
import com.google.gwt.core.client.RunAsyncCallback
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.event.dom.client.ClickHandler
import com.google.gwt.event.dom.client.KeyUpEvent
import com.google.gwt.event.dom.client.KeyUpHandler
import com.google.gwt.i18n.client.Constants
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Handlers._
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.HorizontalPanel
import com.google.gwt.user.client.ui.Label
import com.google.gwt.user.client.ui.PasswordTextBox
import com.google.gwt.user.client.ui.TextArea
import com.google.gwt.user.client.ui.TextBox
import com.google.gwt.user.client.ui.TextBoxBase
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget

object CwBasicText {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwBasicTextAreaLabel(): String

    def cwBasicTextDescription(): String

    def cwBasicTextName(): String

    def cwBasicTextNormalLabel(): String

    def cwBasicTextPasswordLabel(): String

    def cwBasicTextReadOnly(): String

    def cwBasicTextSelected(): String
  }
}

/**
 * Example file.
 */
@ShowcaseStyle(Array(
    ".gwt-TextBox", ".gwt-PasswordTextBox", ".gwt-TextArea"))
class CwBasicText(constants: CwBasicText.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwBasicTextDescription

  override def getName() = constants.cwBasicTextName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a panel to layout the widgets
    val vpanel = new VerticalPanel()
    vpanel.setSpacing(5)

    // Add a normal and disabled text box
    val normalText = new TextBox()
    normalText.ensureDebugId("cwBasicText-textbox")
    val disabledText = new TextBox()
    disabledText.ensureDebugId("cwBasicText-textbox-disabled")
    disabledText.setText(constants.cwBasicTextReadOnly())
    disabledText.setEnabled(false)
    vpanel.add(new HTML(constants.cwBasicTextNormalLabel()))
    vpanel.add(createTextExample(normalText, true))
    vpanel.add(createTextExample(disabledText, false))

    // Add a normal and disabled password text box
    val normalPassword = new PasswordTextBox()
    normalPassword.ensureDebugId("cwBasicText-password")
    val disabledPassword = new PasswordTextBox()
    disabledPassword.ensureDebugId("cwBasicText-password-disabled")
    disabledPassword.setText(constants.cwBasicTextReadOnly())
    disabledPassword.setEnabled(false)
    vpanel.add(new HTML("<br><br>" + constants.cwBasicTextPasswordLabel()))
    vpanel.add(createTextExample(normalPassword, true))
    vpanel.add(createTextExample(disabledPassword, false))

    // Add a text area
    val textArea = new TextArea()
    textArea.ensureDebugId("cwBasicText-textarea")
    textArea.setVisibleLines(5)
    vpanel.add(new HTML("<br><br>" + constants.cwBasicTextAreaLabel()))
    vpanel.add(createTextExample(textArea, true))

    // Return the panel
    return vpanel
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) = {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }

  /**
   * Create a TextBox example that includes the text box and an optional
   * handler that updates a Label with the currently selected text.
   * 
   * @param textBox the text box to handle
   * @param addSelection add handlers to update label
   * @return the Label that will be updated
   */
  @ShowcaseSource
  private def createTextExample(textBox: TextBoxBase, addSelection: Boolean): HorizontalPanel = {
    // Add the text box and label to a panel
    val hPanel = new HorizontalPanel()
    hPanel.setSpacing(4)
    hPanel.add(textBox)

    // Add handlers
    if (addSelection) {
      // Create the new label
      val label = new Label(constants.cwBasicTextSelected() + ": 0, 0")

      // Add a KeyUpHandler
      textBox onKeyUp { updateSelectionLabel(textBox, label) }

      // Add a ClickHandler
      textBox onClick { updateSelectionLabel(textBox, label) }

      // Add the label to the box
      hPanel.add(label)
    }

    // Return the panel
    return hPanel
  }

  /**
   * Update the text in one of the selection labels.
   * 
   * @param textBox the text box
   * @param label the label to update
   */
  @ShowcaseSource
  private def updateSelectionLabel(textBox: TextBoxBase, label: Label) {
    label.setText(constants.cwBasicTextSelected() + ": "
        + textBox.getCursorPos() + ", " + textBox.getSelectionLength())
  }
}
