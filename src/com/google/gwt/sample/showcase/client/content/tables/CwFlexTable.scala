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
package com.google.gwt.sample.showcase.client.content.tables

import com.google.gwt.core.client.GWT
import com.google.gwt.core.client.RunAsyncCallback
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.event.dom.client.ClickHandler
import com.google.gwt.i18n.client.Constants
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Showcase
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.Button
import com.google.gwt.user.client.ui.FlexTable
import com.google.gwt.user.client.ui.HasHorizontalAlignment
import com.google.gwt.user.client.ui.HasVerticalAlignment
import com.google.gwt.user.client.ui.Image
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget

object CwFlexTable {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwFlexTableAddRow(): String

    def cwFlexTableDescription(): String

    def cwFlexTableDetails(): String

    def cwFlexTableName(): String

    def cwFlexTableRemoveRow(): String
  }
}

/**
 * Example file.
 */
@ShowcaseStyle(Array(".cw-FlexTable"))
class CwFlexTable(constants: CwFlexTable.CwConstants) extends ContentWidget(constants) {

  override def getDescription() = constants.cwFlexTableDescription

  override def getName() = constants.cwFlexTableName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a Flex Table
    val flexTable = new FlexTable()
    flexTable.addStyleName("cw-FlexTable")
    flexTable.setWidth("32em")
    flexTable.setCellSpacing(5)
    flexTable.setCellPadding(3)

    // Add some text
    flexTable.getFlexCellFormatter().setHorizontalAlignment(0, 1,
        HasHorizontalAlignment.ALIGN_LEFT)
    flexTable.setHTML(0, 0, constants.cwFlexTableDetails())
    flexTable.getFlexCellFormatter().setColSpan(0, 0, 2)

    // Add a button that will add more rows to the table
    val addRowButton = new Button(constants.cwFlexTableAddRow(),
        new ClickHandler() {
          def onClick(event: ClickEvent) = addRow(flexTable)
        })
    addRowButton.addStyleName("sc-FixedWidthButton")

    val removeRowButton = new Button(constants.cwFlexTableRemoveRow(),
        new ClickHandler() {
          def onClick(event: ClickEvent) = removeRow(flexTable)
        })
    removeRowButton.addStyleName("sc-FixedWidthButton")
    val buttonPanel = new VerticalPanel()
    buttonPanel.setStyleName("cw-FlexTable-buttonPanel")
    buttonPanel.add(addRowButton)
    buttonPanel.add(removeRowButton)
    flexTable.setWidget(0, 1, buttonPanel)
    flexTable.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP)

    // Add two rows to start
    addRow(flexTable)
    addRow(flexTable)

    // Return the panel
    flexTable.ensureDebugId("cwFlexTable")
    return flexTable
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) = {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }

  /**
   * Add a row to the flex table.
   */
  @ShowcaseSource
  private def addRow(flexTable: FlexTable) = {
    val numRows = flexTable.getRowCount()
    flexTable.setWidget(numRows, 0, new Image(Showcase.images.gwtLogo()))
    flexTable.setWidget(numRows, 1, new Image(Showcase.images.gwtLogo()))
    flexTable.getFlexCellFormatter().setRowSpan(0, 1, numRows + 1)
  }

  /**
   * Remove a row from the flex table.
   */
  @ShowcaseSource
  private def removeRow(flexTable: FlexTable) = {
    val numRows = flexTable.getRowCount()
    if (numRows > 1) {
      flexTable.removeRow(numRows - 1)
      flexTable.getFlexCellFormatter().setRowSpan(0, 1, numRows - 1)
    }
  }
}
