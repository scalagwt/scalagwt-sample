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
import com.google.gwt.event.dom.client.ChangeEvent
import com.google.gwt.event.dom.client.ChangeHandler
import com.google.gwt.event.dom.client.KeyUpEvent
import com.google.gwt.event.dom.client.KeyUpHandler
import com.google.gwt.i18n.client.Constants
import com.google.gwt.i18n.client.DateTimeFormat
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.Grid
import com.google.gwt.user.client.ui.HasVerticalAlignment
import com.google.gwt.user.client.ui.Label
import com.google.gwt.user.client.ui.ListBox
import com.google.gwt.user.client.ui.TextBox
import com.google.gwt.user.client.ui.Widget

import java.util.Date

object CwDateTimeFormat {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwDateTimeFormatDescription(): String

    def cwDateTimeFormatFailedToParseInput(): String

    def cwDateTimeFormatFormattedLabel(): String

    def cwDateTimeFormatInvalidPattern(): String

    def cwDateTimeFormatName(): String

    def cwDateTimeFormatPatternLabel(): String

    def cwDateTimeFormatPatterns(): Array[String]

    def cwDateTimeFormatValueLabel(): String
  }
}

/**
 * Example file.
 */
@ShowcaseStyle(Array(".cw-RedText"))
class CwDateTimeFormat(constants: CwDateTimeFormat.CwConstants) extends ContentWidget(constants) {

  /**
   * The {@link DateTimeFormat} that is currently being applied.
   */
  private var activeFormat: DateTimeFormat = null

  /**
   * The {@link Label} where the formatted value is displayed.
   */
  @ShowcaseData
  private var formattedBox: Label = null

  /**
   * The {@link TextBox} that displays the current pattern.
   */
  @ShowcaseData
  private var patternBox: TextBox = null

  /**
   * The {@link ListBox} that holds the patterns.
   */
  @ShowcaseData
  private var patternList: ListBox = null

  /**
   * The {@link TextBox} where the user enters a value.
   */
  @ShowcaseData
  private var valueBox: TextBox = null

  override def getDescription() = constants.cwDateTimeFormatDescription

  override def getName() = constants.cwDateTimeFormatName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Use a Grid to layout the content
    val layout = new Grid(4, 2)
    val formatter = layout.getCellFormatter()
    layout.setCellSpacing(5)

    // Add a field to select the pattern
    patternList = new ListBox()
    patternList.setWidth("17em")
    val patterns = constants.cwDateTimeFormatPatterns
    for (pattern <- patterns) {
      patternList.addItem(pattern)
    }
    patternList.addChangeHandler(new ChangeHandler() {
      def onChange(event: ChangeEvent) = updatePattern()
    })
    layout.setHTML(0, 0, constants.cwDateTimeFormatPatternLabel)
    layout.setWidget(0, 1, patternList)

    // Add a field to display the pattern
    patternBox = new TextBox()
    patternBox.setWidth("17em")
    patternBox.addKeyUpHandler(new KeyUpHandler() {
      def onKeyUp(event: KeyUpEvent) = updatePattern()
    })

    layout.setWidget(1, 1, patternBox)

    // Add a field to set the value
    valueBox = new TextBox()
    valueBox.setWidth("17em")
    valueBox.setText("13 September 1999 12:34:56")
    valueBox.addKeyUpHandler(new KeyUpHandler() {
      def onKeyUp(event: KeyUpEvent) = updateFormattedValue()
    })

    layout.setHTML(2, 0, constants.cwDateTimeFormatValueLabel)
    layout.setWidget(2, 1, valueBox)

    // Add a field to display the formatted value
    formattedBox = new Label()
    formattedBox.setWidth("17em")
    layout.setHTML(3, 0, constants.cwDateTimeFormatFormattedLabel)
    layout.setWidget(3, 1, formattedBox)
    formatter.setVerticalAlignment(3, 0, HasVerticalAlignment.ALIGN_TOP)

    // Return the layout Widget
    updatePattern()
    return layout
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) = {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }


  /**
   * Show an error message. Pass in null to clear the error message.
   * 
   * @param errorMsg the error message
   */
  @ShowcaseSource
  private def showErrorMessage(errorMsg: String) {
    if (errorMsg == null) {
      formattedBox.removeStyleName("cw-RedText")
    } else {
      formattedBox.setText(errorMsg)
      formattedBox.addStyleName("cw-RedText")
    }
  }

  /**
   * Update the formatted value based on the user entered value and pattern.
   */
  @ShowcaseSource
  private def updateFormattedValue() {
    val sValue = valueBox.getText()
    if (!sValue.equals("")) {
      try {
        val date = new Date(sValue)
        val formattedValue = activeFormat.format(date)
        formattedBox.setText(formattedValue)
        showErrorMessage(null)
      } catch {
        case e: IllegalArgumentException =>
          showErrorMessage(constants.cwDateTimeFormatFailedToParseInput)
      }
    } else {
      formattedBox.setText("<None>")
    }
  }

  /**
   * Update the selected pattern based on the pattern in the list.
   */
  @ShowcaseSource
  private def updatePattern() {
    patternList.getSelectedIndex match {
      // Date + Time
      case 0 =>
        activeFormat = DateTimeFormat.getFullDateTimeFormat
        patternBox.setText(activeFormat.getPattern)
        patternBox.setEnabled(false)
      case 1 =>
        activeFormat = DateTimeFormat.getLongDateTimeFormat
        patternBox.setText(activeFormat.getPattern)
        patternBox.setEnabled(false)
      case 2 =>
        activeFormat = DateTimeFormat.getMediumDateTimeFormat
        patternBox.setText(activeFormat.getPattern)
        patternBox.setEnabled(false)
      case 3 =>
        activeFormat = DateTimeFormat.getShortDateTimeFormat
        patternBox.setText(activeFormat.getPattern)
        patternBox.setEnabled(false)

      // Date only
      case 4 =>
        activeFormat = DateTimeFormat.getFullDateFormat
        patternBox.setText(activeFormat.getPattern)
        patternBox.setEnabled(false)
      case 5 =>
        activeFormat = DateTimeFormat.getLongDateFormat
        patternBox.setText(activeFormat.getPattern)
        patternBox.setEnabled(false)
      case 6 =>
        activeFormat = DateTimeFormat.getMediumDateFormat
        patternBox.setText(activeFormat.getPattern)
        patternBox.setEnabled(false)
      case 7 =>
        activeFormat = DateTimeFormat.getShortDateFormat
        patternBox.setText(activeFormat.getPattern)
        patternBox.setEnabled(false)

      // Time only
      case 8 =>
        activeFormat = DateTimeFormat.getFullTimeFormat
        patternBox.setText(activeFormat.getPattern)
        patternBox.setEnabled(false)
      case 9 =>
        activeFormat = DateTimeFormat.getLongTimeFormat
        patternBox.setText(activeFormat.getPattern)
        patternBox.setEnabled(false)
      case 10 =>
        activeFormat = DateTimeFormat.getMediumTimeFormat
        patternBox.setText(activeFormat.getPattern)
        patternBox.setEnabled(false)
      case 11 =>
        activeFormat = DateTimeFormat.getShortTimeFormat
        patternBox.setText(activeFormat.getPattern)
        patternBox.setEnabled(false)

      // Custom
      case 12 =>
        patternBox.setEnabled(true)
        val pattern = patternBox.getText
        try {
          activeFormat = DateTimeFormat.getFormat(pattern)
        } catch {
          case e: IllegalArgumentException =>
            showErrorMessage(constants.cwDateTimeFormatInvalidPattern)
        }
    }

    // Update the formatted value
    updateFormattedValue()
  }
}
