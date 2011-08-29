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
import com.google.gwt.i18n.client.Constants
import com.google.gwt.i18n.client.Dictionary
import com.google.gwt.sample.showcase.client.GWTConversions._
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.FlexTable
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget

import scala.collection.JavaConversions._

object CwDictionaryExample {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwDictionaryExampleDescription(): String

    def cwDictionaryExampleLinkText(): String

    def cwDictionaryExampleName(): String
  }
}

/**
 * Example file.
 */
@ShowcaseStyle(Array(".cw-DictionaryExample"))
class CwDictionaryExample(constants: CwDictionaryExample.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwDictionaryExampleDescription

  override def getName() = constants.cwDictionaryExampleName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a vertical panel to layout the contents
    val layout = new VerticalPanel()

    // Show the HTML variable that defines the dictionary
    val source = new HTML("<pre>var userInfo = {\n"
        + "&nbsp;&nbsp;name: \"Amelie Crutcher\",\n"
        + "&nbsp;&nbsp;timeZone: \"EST\",\n" + "&nbsp;&nbsp;userID: \"123\",\n"
        + "&nbsp;&nbsp;lastLogOn: \"2/2/2006\"\n" + "};</pre>\n")
    source.getElement.setDir("ltr")
    source.getElement.getStyle.setProperty("textAlign", "left")
    layout.add(new HTML(constants.cwDictionaryExampleLinkText))
    layout.add(source)

    // Create the Dictionary of data
    val userInfoGrid = new FlexTable()
    val userInfo = Dictionary.getDictionary("userInfo")
    var columnCount = 0
    for ((k, v) <- userInfo) {
      // Add a column with the data
      userInfoGrid.setHTML(0, columnCount, k)
      userInfoGrid.setHTML(1, columnCount, v)

      // Go to the next column
      columnCount += 1
    }
    userInfoGrid.getRowFormatter.setStyleName(0, "cw-DictionaryExample-headerRow")
    userInfoGrid.getRowFormatter.setStyleName(1, "cw-DictionaryExample-dataRow")
    layout.add(new HTML("<br><br>"))
    layout.add(userInfoGrid)

    // Return the layout Widget
    layout
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) = {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }
}
