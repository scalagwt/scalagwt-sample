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
package com.google.gwt.sample.showcase.client.content.widgets

import com.google.gwt.core.client.GWT
import com.google.gwt.core.client.RunAsyncCallback
import com.google.gwt.event.logical.shared.ValueChangeEvent
import com.google.gwt.event.logical.shared.ValueChangeHandler
import com.google.gwt.i18n.client.Constants
import com.google.gwt.i18n.client.DateTimeFormat
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Handlers._
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.Label
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget
import com.google.gwt.user.datepicker.client.DateBox
import com.google.gwt.user.datepicker.client.DatePicker

import java.util.Date

object CwDatePicker {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwDatePickerBoxLabel(): String

    def cwDatePickerDescription(): String

    def cwDatePickerLabel(): String

    def cwDatePickerName(): String
  }
}

/**
 * Example file.
 */
@ShowcaseStyle(Array(".gwt-DatePicker", ".datePicker", "td.datePickerMonth", ".gwt-DateBox", ".dateBox"))
class CwDatePicker(constants: CwDatePicker.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwDatePickerDescription

  override def getName() = constants.cwDatePickerName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a basic date picker
    val datePicker = new DatePicker()
    val text = new Label()

    // Set the value in the text box when the user selects a date
    datePicker addValueChangeHandler { event: ValueChangeEvent[Date] =>
      val date = event.getValue()
      val dateString = DateTimeFormat.getMediumDateFormat().format(date)
      text.setText(dateString)
    }

    // Set the default value
    datePicker.setValue(new Date(), true)

    // Create a DateBox
    val dateFormat = DateTimeFormat.getLongDateFormat()
    val dateBox = new DateBox()
    dateBox.setFormat(new DateBox.DefaultFormat(dateFormat))

    // Combine the widgets into a panel and return them
    val vPanel = new VerticalPanel()
    vPanel.add(new HTML(constants.cwDatePickerLabel))
    vPanel.add(text)
    vPanel.add(datePicker)
    vPanel.add(new HTML(constants.cwDatePickerBoxLabel))
    vPanel.add(dateBox)
    return vPanel
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) = {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }
}
