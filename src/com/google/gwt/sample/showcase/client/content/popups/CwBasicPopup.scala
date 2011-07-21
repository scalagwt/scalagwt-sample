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
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Handlers._
import com.google.gwt.sample.showcase.client.Showcase
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.Button
import com.google.gwt.user.client.ui.DecoratedPopupPanel
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.Image
import com.google.gwt.user.client.ui.PopupPanel
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget

object CwBasicPopup {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwBasicPopupClickOutsideInstructions(): String

    def cwBasicPopupDescription(): String

    def cwBasicPopupInstructions(): String

    def cwBasicPopupName(): String

    def cwBasicPopupShowButton(): String
  }
}

/**
 * Example file.
 */
@ShowcaseStyle(Array(
    ".gwt-PopupPanel", "html>body .gwt-PopupPanel", "* html .gwt-PopupPanel",
    ".gwt-DecoratedPopupPanel", "html>body .gwt-DecoratedPopupPanel",
    "* html .gwt-DecoratedPopupPanel"))
class CwBasicPopup(constants: CwBasicPopup.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwBasicPopupDescription

  override def getName() = constants.cwBasicPopupName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a basic popup widget
    val simplePopup = new DecoratedPopupPanel(true)
    simplePopup.ensureDebugId("cwBasicPopup-simplePopup")
    simplePopup.setWidth("150px")
    simplePopup.setWidget(new HTML(
        constants.cwBasicPopupClickOutsideInstructions))

    // Create a button to show the popup
    val openButton = new Button(constants.cwBasicPopupShowButton,
      { event: ClickEvent =>
          // Reposition the popup relative to the button
          val source = event.getSource.asInstanceOf[Widget]
          val left = source.getAbsoluteLeft + 10
          val top = source.getAbsoluteTop + 10
          simplePopup.setPopupPosition(left, top)

          // Show the popup
          simplePopup.show()
      })

    // Create a popup to show the full size image
    val jimmyFull = new Image(Showcase.images.jimmy)
    val imagePopup = new PopupPanel(true)
    imagePopup.setAnimationEnabled(true)
    imagePopup.ensureDebugId("cwBasicPopup-imagePopup")
    imagePopup.setWidget(jimmyFull)
    jimmyFull onClick { _ => imagePopup.hide() }

    // Add an image thumbnail
    val jimmyThumb = new Image(Showcase.images.jimmyThumb)
    jimmyThumb.ensureDebugId("cwBasicPopup-thumb")
    jimmyThumb.addStyleName("cw-BasicPopup-thumb")
    jimmyThumb onClick { _ => imagePopup.center() }

    // Add the widgets to a panel
    val vPanel = new VerticalPanel()
    vPanel.setSpacing(5)
    vPanel.add(openButton)
    vPanel.add(new HTML("<br><br><br>" + constants.cwBasicPopupInstructions))
    vPanel.add(jimmyThumb)

    // Return the panel
    return vPanel
  }

  override def asyncOnInitialize(callback:AsyncCallback[Widget]) {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }
}
