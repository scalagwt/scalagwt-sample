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
package com.google.gwt.sample.showcase.client.content.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.sample.showcase.client.ContentWidget;
import com.google.gwt.sample.showcase.client.Showcase;
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData;
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import scala.collection.mutable.Map
import scala.collection.mutable.LinkedHashMap

object CwAbsolutePanel {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwAbsolutePanelClickMe(): String

    def cwAbsolutePanelDescription(): String

    def cwAbsolutePanelHelloWorld(): String

    def cwAbsolutePanelItemsToMove(): String

    def cwAbsolutePanelLeft(): String

    def cwAbsolutePanelName(): String

    def cwAbsolutePanelTop(): String

    def cwAbsolutePanelWidgetNames(): Array[String]
  }
}

/**
 * Example file.
 */
class CwAbsolutePanel(constants: CwAbsolutePanel.CwConstants) extends ContentWidget(constants) {

  /**
   * The absolute panel used in the example.
   */
  private var absolutePanel: AbsolutePanel = null;

  /**
   * The input field used to set the left position of a {@link Widget}.
   */
  @ShowcaseData
  private var leftPosBox: TextBox  = null;

  /**
   * The list box of items that can be repositioned.
   */
  @ShowcaseData
  private val listBox = new ListBox();

  /**
   * The input field used to set the top position of a {@link Widget}.
   */
  @ShowcaseData
  private var topPosBox: TextBox = null;

  /**
   * A mapping between the name of a {@link Widget} and the widget in the
   * {@link AbsolutePanel}.
   */
  @ShowcaseData
  private var widgetMap: Map[String, Widget] = null;

  override def getDescription() = constants.cwAbsolutePanelDescription

  override def getName() = constants.cwAbsolutePanelName

  override def hasStyle() = false

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a new panel
    widgetMap = new LinkedHashMap[String, Widget]();
    absolutePanel = new AbsolutePanel();
    absolutePanel.setSize("250px", "250px");
    absolutePanel.ensureDebugId("cwAbsolutePanel");

    // Add an HTML widget to the panel
    val widgetNames = constants.cwAbsolutePanelWidgetNames;
    val text = new HTML(constants.cwAbsolutePanelHelloWorld);
    absolutePanel.add(text, 10, 20);
    widgetMap.put(widgetNames(0), text);

    // Add a Button to the panel
    val button = new Button(constants.cwAbsolutePanelClickMe);
    absolutePanel.add(button, 80, 45);
    widgetMap.put(widgetNames(1), button);

    // Add a Button to the panel
    val grid = new Grid(2, 3);
    grid.setBorderWidth(1);
    for (i <- 0 until 3) {
      grid.setHTML(0, i, i + "");
      grid.setWidget(1, i, new Image(Showcase.images.gwtLogoThumb()));
    }
    absolutePanel.add(grid, 60, 100);
    widgetMap.put(widgetNames(2), grid);

    // Wrap the absolute panel in a DecoratorPanel
    val absolutePanelWrapper = new DecoratorPanel();
    absolutePanelWrapper.setWidget(absolutePanel);

    // Create the options bar
    val optionsWrapper = new DecoratorPanel();
    optionsWrapper.setWidget(createOptionsBar());

    // Add the components to a panel and return it
    val mainLayout = new HorizontalPanel();
    mainLayout.setSpacing(10);
    mainLayout.add(optionsWrapper);
    mainLayout.add(absolutePanelWrapper);

    return mainLayout;
  }

  /**
   * Initialize the options panel after the {@link AbsolutePanel} has been
   * attached to the page.
   */
  @ShowcaseSource
  override def onInitializeComplete() {
    DeferredCommand.addCommand(new Command() {
      def execute() {
        updateSelectedItem();
      }
    });
  }

  override def asyncOnInitialize(callback: AsyncCallback[Widget]) {
    GWT.runAsync(new RunAsyncCallback() {
      def onFailure(caught: Throwable) = callback.onFailure(caught)

      def onSuccess() = callback.onSuccess(onInitialize())
    })
  }

  /**
   * Create an options panel that allows users to select a widget and reposition
   * it.
   * 
   * @return the new options panel
   */
  @ShowcaseSource
  private def createOptionsBar(): Widget = {
    // Create a panel to move components around
    val optionsBar = new FlexTable();
    topPosBox = new TextBox();
    topPosBox.setWidth("3em");
    topPosBox.setText("100");
    leftPosBox = new TextBox();
    leftPosBox.setWidth("3em");
    leftPosBox.setText("60");
    optionsBar.setHTML(0, 0, constants.cwAbsolutePanelItemsToMove());
    optionsBar.setWidget(0, 1, listBox);
    optionsBar.setHTML(1, 0, constants.cwAbsolutePanelTop());
    optionsBar.setWidget(1, 1, topPosBox);
    optionsBar.setHTML(2, 0, constants.cwAbsolutePanelLeft());
    optionsBar.setWidget(2, 1, leftPosBox);

    // Add the widgets to the list box
    for (name <- widgetMap.keys) {
      listBox.addItem(name);
    }

    // Set the current item position when the user selects an item
    listBox.addChangeHandler(new ChangeHandler() {
      def onChange(event: ChangeEvent) {
        updateSelectedItem();
      }
    });

    // Move the item as the user changes the value in the left and top boxes
    val repositionHandler = new KeyUpHandler() {
      def onKeyUp(event: KeyUpEvent) {
        repositionItem();
      }
    };
    topPosBox.addKeyUpHandler(repositionHandler);
    leftPosBox.addKeyUpHandler(repositionHandler);

    // Return the options bar
    return optionsBar;
  }

  /**
   * Reposition an item when the user changes the value in the top or left
   * position text boxes.
   */
  @ShowcaseSource
  private def repositionItem() {
    // Get the selected item to move
    val name = listBox.getValue(listBox.getSelectedIndex);
    val item = widgetMap.get(name).get;

    // Reposition the item
    try {
      val top = Integer.parseInt(topPosBox.getText);
      val left = Integer.parseInt(leftPosBox.getText);
      absolutePanel.setWidgetPosition(item, left, top);
    } catch {
      case e: NumberFormatException => // Ignore invalid user input
    }
  }

  /**
   * Update the top and left position text fields when the user selects a new
   * item to move.
   */
  @ShowcaseSource
  private def updateSelectedItem() {
    val name = listBox.getValue(listBox.getSelectedIndex);
    val item = widgetMap.get(name).get
    topPosBox.setText(absolutePanel.getWidgetTop(item) + "");
    leftPosBox.setText(absolutePanel.getWidgetLeft(item) + "");
  }
}
