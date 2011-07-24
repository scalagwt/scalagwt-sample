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
package com.google.gwt.sample.showcase.client.content.popups

import com.google.gwt.core.client.GWT
import com.google.gwt.core.client.RunAsyncCallback
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.event.dom.client.ClickHandler
import com.google.gwt.i18n.client.Constants
import com.google.gwt.i18n.client.LocaleInfo
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Handlers._
import com.google.gwt.sample.showcase.client.Showcase
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.Button
import com.google.gwt.user.client.ui.DialogBox
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.HasHorizontalAlignment
import com.google.gwt.user.client.ui.Image
import com.google.gwt.user.client.ui.ListBox
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget

object CwDialogBox {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwDialogBoxCaption(): String

    def cwDialogBoxClose(): String

    def cwDialogBoxDescription(): String

    def cwDialogBoxDetails(): String

    def cwDialogBoxItem(): String

    def cwDialogBoxListBoxInfo(): String

    def cwDialogBoxMakeTransparent(): String

    def cwDialogBoxName(): String

    def cwDialogBoxShowButton(): String
  }
}

/**
 * Example file.
 */
@ShowcaseStyle(Array(
    ".gwt-DialogBox", "html>body .gwt-DialogBox", "* html .gwt-DialogBox",
    ".cw-DialogBox"))
class CwDialogBox(constants: CwDialogBox.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwDialogBoxDescription

  override def getName() = constants.cwDialogBoxName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create the dialog box
    val dialogBox = createDialogBox()
    dialogBox.setGlassEnabled(true)
    dialogBox.setAnimationEnabled(true)

    // Create a button to show the dialog Box
    val openButton = new Button(constants.cwDialogBoxShowButton,
      () => {
        dialogBox.center()
        dialogBox.show()
      })

    // Create a ListBox
    val listDesc = new HTML("<br><br><br>" + constants.cwDialogBoxListBoxInfo)

    val list = new ListBox()
    list.setVisibleItemCount(1)
    for (i <- 10 until 0) {
      list.addItem(constants.cwDialogBoxItem + " " + i)
    }

    // Add the button and list to a panel
    val vPanel = new VerticalPanel()
    vPanel.setSpacing(8)
    vPanel.add(openButton)
    vPanel.add(listDesc)
    vPanel.add(list)

    // Return the panel
    return vPanel
  }
  
  override def asyncOnInitialize(callback:AsyncCallback[Widget]) {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }

  /**
   * Create the dialog box for this example.
   * 
   * @return the new dialog box
   */
  @ShowcaseSource
  private def createDialogBox(): DialogBox = {
    // Create a dialog box and set the caption text
    val dialogBox = new DialogBox()
    dialogBox.ensureDebugId("cwDialogBox")
    dialogBox.setText(constants.cwDialogBoxCaption)

    // Create a table to layout the content
    val dialogContents = new VerticalPanel()
    dialogContents.setSpacing(4)
    dialogBox.setWidget(dialogContents)

    // Add some text to the top of the dialog
    val details = new HTML(constants.cwDialogBoxDetails)
    dialogContents.add(details)
    dialogContents.setCellHorizontalAlignment(details,
        HasHorizontalAlignment.ALIGN_CENTER)

    // Add an image to the dialog
    val image = new Image(Showcase.images.jimmy)
    dialogContents.add(image)
    dialogContents.setCellHorizontalAlignment(image,
        HasHorizontalAlignment.ALIGN_CENTER)

    // Add a close button at the bottom of the dialog
    val closeButton = new Button(constants.cwDialogBoxClose,
                                 () => dialogBox.hide())
    dialogContents.add(closeButton)
    if (LocaleInfo.getCurrentLocale().isRTL()) {
      dialogContents.setCellHorizontalAlignment(closeButton,
          HasHorizontalAlignment.ALIGN_LEFT)
    } else {
      dialogContents.setCellHorizontalAlignment(closeButton,
          HasHorizontalAlignment.ALIGN_RIGHT)
    }

    // Return the dialog box
    return dialogBox
  }
}
