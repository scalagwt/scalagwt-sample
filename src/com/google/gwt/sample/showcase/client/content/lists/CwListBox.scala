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


package com.google.gwt.sample.showcase.client.content.lists

import com.google.gwt.core.client.GWT
import com.google.gwt.core.client.RunAsyncCallback
import com.google.gwt.event.dom.client.ChangeEvent
import com.google.gwt.event.dom.client.ChangeHandler
import com.google.gwt.i18n.client.Constants
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Handlers._
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.HorizontalPanel
import com.google.gwt.user.client.ui.ListBox
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget

/**
 * Example file.
 */
object CwListBox {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwListBoxSelectAll: String

    def cwListBoxVacations: Array[String]

    def cwListBoxSelectCategory: String

    def cwListBoxDescription: String

    def cwListBoxName: String

    def cwListBoxCategories: Array[String]

    def cwListBoxSports: Array[String]

    def cwListBoxCars: Array[String]
  }
}

@ShowcaseStyle(Array(".gwt-ListBox"))
class CwListBox(@ShowcaseData private val constants: CwListBox.CwConstants)
      extends ContentWidget(constants) {

  def getDescription: String = constants.cwListBoxDescription
  def getName: String = constants.cwListBoxName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  def onInitialize: Widget = {
    // Create a panel to align the Widgets
    val hPanel = new HorizontalPanel
    hPanel.setSpacing(20)

    // Add a drop box with the list types
    val dropBox = new ListBox(false)
    val listTypes: Array[String] = constants.cwListBoxCategories
    listTypes foreach { dropBox.addItem(_) }
    dropBox.ensureDebugId("cwListBox-dropBox")
    val dropBoxPanel = new VerticalPanel
    dropBoxPanel.setSpacing(4)
    dropBoxPanel.add(new HTML(constants.cwListBoxSelectCategory))
    dropBoxPanel.add(dropBox)
    hPanel.add(dropBoxPanel)

    // Add a list box with multiple selection enabled
    val multiBox = new ListBox(true)
    multiBox.ensureDebugId("cwListBox-multiBox")
    multiBox.setWidth("11em")
    multiBox.setVisibleItemCount(10)
    val multiBoxPanel = new VerticalPanel
    multiBoxPanel.setSpacing(4)
    multiBoxPanel.add(new HTML(constants.cwListBoxSelectAll))
    multiBoxPanel.add(multiBox)
    hPanel.add(multiBoxPanel)

    // Add a handler to handle drop box events
    dropBox onChange { _ =>
      showCategory(multiBox, dropBox.getSelectedIndex)
      multiBox.ensureDebugId("cwListBox-multiBox")
    }

    // Show default category
    showCategory(multiBox, 0)
    multiBox.ensureDebugId("cwListBox-multiBox")

    // Return the panel
    hPanel
  }

  protected override def asyncOnInitialize(callback: AsyncCallback[Widget]): Unit = {
    GWT.runAsync(new RunAsyncCallback {
      def onFailure(caught: Throwable): Unit = {
        callback.onFailure(caught)
      }

      def onSuccess: Unit = {
        callback.onSuccess(onInitialize)
      }
    })
  }

  /**
   * Display the options for a given category in the list box.
   *
   * @param listBox the ListBox to add the options to
   * @param category the category index
   */
  @ShowcaseSource
  private def showCategory(listBox: ListBox, category: Int): Unit = {
    listBox.clear
    val listData = category match {
      case 0 => constants.cwListBoxCars
      case 1 => constants.cwListBoxSports
      case 2 => constants.cwListBoxVacations
    }
    listData foreach { listBox.addItem(_) }
  }
}

