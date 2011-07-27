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
package com.google.gwt.sample.showcase.client

import com.google.gwt.dom.client.Document
import com.google.gwt.dom.client.HeadElement
import com.google.gwt.dom.client.LinkElement
import com.google.gwt.user.client.Command
import com.google.gwt.user.client.DeferredCommand
import com.google.gwt.user.client.Timer
import com.google.gwt.user.client.ui.Label
import com.google.gwt.user.client.ui.RootPanel

/**
 * A utility class that loads style sheets.
 */
object StyleSheetLoader {

  /**
   * A {@link Timer} that creates a small reference widget used to determine
   * when a new style sheet has finished loading. The widget has a natural width
   * of 0px, but when the style sheet is loaded, the width changes to 5px. The
   * style sheet should contain a style definition that is passed into the
   * constructor that defines a height and width greater than 0px.
   *
   * @param refStyleName the reference style name
   * @param callback the callback to execute when the style sheet loads
   */
  private class StyleTesterTimer(refStyleName: String, private val callback: Command) extends Timer {
    private val refWidget = new Label

    refWidget.setStyleName(refStyleName)
    refWidget.getElement.getStyle.setProperty("position", "absolute")
    refWidget.getElement.getStyle.setProperty("visibility", "hidden")
    refWidget.getElement.getStyle.setProperty("display", "inline")
    refWidget.getElement.getStyle.setPropertyPx("padding", 0)
    refWidget.getElement.getStyle.setPropertyPx("margin", 0)
    refWidget.getElement.getStyle.setPropertyPx("border", 0)
    refWidget.getElement.getStyle.setPropertyPx("top", 0)
    refWidget.getElement.getStyle.setPropertyPx("left", 0)
    RootPanel.get.add(refWidget)

    def run: Unit = {
      refWidget.setVisible(false)
      refWidget.setVisible(true)
      if (refWidget.getOffsetWidth > 0) {
        RootPanel.get.remove(refWidget)
        DeferredCommand.addCommand(callback)
      }
      else {
        schedule(10)
      }
    }
  }
  /**
   * Load a style sheet onto the page.
   *
   * @param href the url of the style sheet
   */
  def loadStyleSheet(href: String): Unit = {
    var linkElem: LinkElement = Document.get.createLinkElement
    linkElem.setRel("stylesheet")
    linkElem.setType("text/css")
    linkElem.setHref(href)
    DomUtil.getHeadElement.appendChild(linkElem)
  }

  /**
   * Load a style sheet onto the page and fire a callback when it has loaded.
   * The style sheet should contain a style definition called refStyleName that
   * defines a height and width greater than 0px.
   *
   * @param href the url of the style sheet
   * @param refStyleName the style name of the reference element
   * @param callback the callback executed when the style sheet has loaded
   */
  def loadStyleSheet(href: String, refStyleName: String, callback: Command): Unit = {
    loadStyleSheet(href)
    waitForStyleSheet(refStyleName, callback)
  }

  /**
   * Detect when a style sheet has loaded by placing an element on the page that
   * is affected by a rule in the style sheet, as described in
   * {@link #loadStyleSheet(String, String, Command)}. When the style sheet has
   * loaded, the callback will be executed.
   *
   * @param refStyleName the style name of the reference element
   * @param callback the callback executed when the style sheet has loaded
   */
  def waitForStyleSheet(refStyleName: String, callback: Command): Unit = {
    new StyleSheetLoader.StyleTesterTimer(refStyleName, callback).run
  }

}