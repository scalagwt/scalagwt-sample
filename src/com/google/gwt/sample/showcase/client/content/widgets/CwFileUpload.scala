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
import com.google.gwt.i18n.client.Constants
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.ClickHandlers._
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.Window
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.Button
import com.google.gwt.event.dom.client.ClickHandler
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.user.client.ui.FileUpload
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget

/**
 * Example file.
 */
@ShowcaseStyle(Array(".gwt-FileUpload"))
object CwFileUpload {

  /*
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwFileUploadButton: String

    def cwFileUploadDescription: String

    def cwFileUploadName: String

    def cwFileUploadNoFileError: String

    def cwFileUploadSelectFile: String

    def cwFileUploadSuccessful: String
  }

}

@ShowcaseStyle(Array(".gwt-FileUpload"))
class CwFileUpload(@ShowcaseData constants: CwFileUpload.CwConstants) extends ContentWidget(constants) {
  override def getDescription: String = constants.cwFileUploadDescription

  override def getName: String = constants.cwFileUploadName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize: Widget = {
    // Create a vertical panel to align the content
    val vPanel = new VerticalPanel

    // Add a label
    vPanel.add(new HTML(constants.cwFileUploadSelectFile))

    // Add a file upload widget
    val fileUpload = new FileUpload
    fileUpload.ensureDebugId("cwFileUpload")
    vPanel.add(fileUpload)

    // Add a button to upload the file
    val uploadButton = new Button(constants.cwFileUploadButton)
    uploadButton onClick { _ =>
      val filename = fileUpload.getFilename
      if (filename.length == 0) {
        Window.alert(constants.cwFileUploadNoFileError)
      } else {
        Window.alert(constants.cwFileUploadSuccessful)
      }
    }
    vPanel.add(new HTML("<br>"))
    vPanel.add(uploadButton)

    // Return the layout panel
    vPanel
  }

  def asyncOnInitialize(callback: AsyncCallback[Widget]): Unit = {
    GWT.runAsync(new RunAsyncCallback {
      def onFailure(caught: Throwable): Unit = callback.onFailure(caught)
      def onSuccess: Unit = callback.onSuccess(onInitialize)
    })
  }
}

