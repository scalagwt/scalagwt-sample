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
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.event.dom.client.ClickHandler
import com.google.gwt.i18n.client.Constants
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Handlers._
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.Window
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.Button
import com.google.gwt.user.client.ui.HorizontalPanel
import com.google.gwt.user.client.ui.Widget

object CwBasicButton {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwBasicButtonClickMessage(): String

    def cwBasicButtonDescription(): String

    def cwBasicButtonDisabled(): String

    def cwBasicButtonName(): String

    def cwBasicButtonNormal(): String
  }
}

/**
 * Example file.
 */
@ShowcaseStyle(Array(".gwt-Button"))
class CwBasicButton(constants: CwBasicButton.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwBasicButtonDescription

  override def getName() = constants.cwBasicButtonName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a panel to align the widgets
    val hPanel = new HorizontalPanel()
    hPanel.setSpacing(10);

    // Add a normal button
    val normalButton = new Button(
        constants.cwBasicButtonNormal,
        Window.alert(constants.cwBasicButtonClickMessage))
    normalButton.ensureDebugId("cwBasicButton-normal")
    hPanel.add(normalButton)

    // Add a disabled button
    val disabledButton = new Button(constants.cwBasicButtonDisabled)
    disabledButton.ensureDebugId("cwBasicButton-disabled")
    disabledButton.setEnabled(false)
    hPanel.add(disabledButton)

    // Return the panel
    return hPanel
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) = {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }
}
