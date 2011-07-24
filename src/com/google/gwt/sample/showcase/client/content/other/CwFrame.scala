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
package com.google.gwt.sample.showcase.client.content.other

import com.google.gwt.core.client.GWT
import com.google.gwt.core.client.RunAsyncCallback
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.event.dom.client.ClickHandler
import com.google.gwt.event.dom.client.KeyCodes
import com.google.gwt.event.dom.client.KeyDownEvent
import com.google.gwt.event.dom.client.KeyDownHandler
import com.google.gwt.i18n.client.Constants
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Handlers._
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.Button
import com.google.gwt.user.client.ui.Frame
import com.google.gwt.user.client.ui.HorizontalPanel
import com.google.gwt.user.client.ui.TextBox
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget

object CwFrame {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwFrameDescription(): String

    def cwFrameName(): String

    def cwFrameSetLocation(): String
  }
}

/**
 * Example file.
 */
class CwFrame(constants: CwFrame.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwFrameDescription

  override def getName() = constants.cwFrameName

  override def hasStyle() = false

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a new frame
    val url = GWT.getModuleBaseURL()
    val frame = new Frame(url)
    frame.setSize("700px", "300px")
    frame.ensureDebugId("cwFrame")

    // Create a form to set the location of the frame
    val locationBox = new TextBox()
    locationBox.setText(url)
    val setLocationButton = new Button(constants.cwFrameSetLocation)
    val optionsPanel = new HorizontalPanel()
    optionsPanel.setSpacing(8)
    optionsPanel.add(locationBox)
    optionsPanel.add(setLocationButton)

    // Change the location when the user clicks the button
    setLocationButton onClick { frame.setUrl(locationBox.getText) }

    // Change the location when the user presses enter
    locationBox onKeyDown { event: KeyDownEvent =>
      if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
        frame.setUrl(locationBox.getText)
      }
    }

    // Add everything to a panel and return it
    val vPanel = new VerticalPanel()
    vPanel.add(optionsPanel)
    vPanel.add(frame)
    return vPanel
  }

  override def asyncOnInitialize(callback:AsyncCallback[Widget]) {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }
}
