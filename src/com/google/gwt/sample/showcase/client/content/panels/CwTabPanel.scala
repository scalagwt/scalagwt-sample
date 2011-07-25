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
package com.google.gwt.sample.showcase.client.content.panels

import com.google.gwt.core.client.GWT
import com.google.gwt.core.client.RunAsyncCallback
import com.google.gwt.i18n.client.Constants
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Showcase
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.DecoratedTabPanel
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.Image
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget

object CwTabPanel {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwTabPanelDescription(): String

    def cwTabPanelName(): String

    def cwTabPanelTab0(): String

    def cwTabPanelTab2(): String

    def cwTabPanelTabs(): Array[String]
  }
}

/**
 * Example file.
 */
@ShowcaseStyle(Array(
    ".gwt-DecoratedTabBar", "html>body .gwt-DecoratedTabBar",
    "* html .gwt-DecoratedTabBar", ".gwt-TabPanel"))
class CwTabPanel(constants: CwTabPanel.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwTabPanelDescription

  override def getName() = constants.cwTabPanelName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a tab panel
    val tabPanel = new DecoratedTabPanel()
    tabPanel.setWidth("400px")
    tabPanel.setAnimationEnabled(true)

    // Add a home tab
    val tabTitles = constants.cwTabPanelTabs()
    val homeText = new HTML(constants.cwTabPanelTab0)
    tabPanel.add(homeText, tabTitles(0))

    // Add a tab with an image
    val vPanel = new VerticalPanel()
    vPanel.add(new Image(Showcase.images.gwtLogo()))
    tabPanel.add(vPanel, tabTitles(1))

    // Add a tab
    val moreInfo = new HTML(constants.cwTabPanelTab2)
    tabPanel.add(moreInfo, tabTitles(2))

    // Return the content
    tabPanel.selectTab(0)
    tabPanel.ensureDebugId("cwTabPanel")
    return tabPanel
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) = {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }
}
