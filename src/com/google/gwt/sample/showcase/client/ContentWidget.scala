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

import com.google.gwt.core.client.GWT
import com.google.gwt.event.logical.shared.SelectionEvent
import com.google.gwt.event.logical.shared.SelectionHandler
import com.google.gwt.http.client.Request
import com.google.gwt.http.client.RequestBuilder
import com.google.gwt.http.client.RequestCallback
import com.google.gwt.http.client.RequestException
import com.google.gwt.http.client.Response
import com.google.gwt.i18n.client.Constants
import com.google.gwt.i18n.client.HasDirection
import com.google.gwt.i18n.client.LocaleInfo
import com.google.gwt.user.client.DOM
import com.google.gwt.user.client.Window
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.DeckPanel
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.LazyPanel
import com.google.gwt.user.client.ui.TabBar
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget

import java.util.HashMap
import java.util.Map

object ContentWidget {
  /**
   * The constants used in this Content Widget.
   */
  trait CwConstants extends Constants {
    def contentWidgetExample(): String

    def contentWidgetSource(): String

    def contentWidgetStyle(): String
  }

  /**
   * The static loading image displayed when loading CSS or source code.
   */
  lazy val loadingImage = "<img src=\"" + GWT.getModuleBaseURL() + "images/loading.gif\">"
}

/**
 * <p>
 * A widget used to show GWT examples in the ContentPanel. It includes a tab bar
 * with options to view the example, view the source, or view the css style
 * rules.
 * </p>
 * <p>
 * This {@link Widget} uses a lazy initialization mechanism so that the content
 * is not rendered until the onInitialize method is called, which happens the
 * first time the {@link Widget} is added to the page.. The data in the source
 * and css tabs are loaded using an RPC call to the server.
 * </p>
 * <h3>CSS Style Rules</h3>
 * <ul class="css">
 * <li>.sc-ContentWidget { Applied to the entire widget }</li>
 * <li>.sc-ContentWidget-tabBar { Applied to the TabBar }</li>
 * <li>.sc-ContentWidget-deckPanel { Applied to the DeckPanel }</li>
 * <li>.sc-ContentWidget-name { Applied to the name }</li>
 * <li>.sc-ContentWidget-description { Applied to the description }</li>
 * </ul>
 */
abstract class ContentWidget(constants: ContentWidget.CwConstants) extends LazyPanel with SelectionHandler[java.lang.Integer] {

  /**
   * The default style name.
   */
  private val DEFAULT_STYLE_NAME = "sc-ContentWidget"

  /**
   * The tab bar of options.
   */
  protected var tabBar: TabBar = new TabBar()

  /**
   * The deck panel with the contents.
   */
  private var deckPanel: DeckPanel = null

  /**
   * A boolean indicating whether or not the RPC request for the source code has
   * been sent.
   */
  private var sourceLoaded = false

  /**
   * The widget used to display source code.
   */
  private var sourceWidget: HTML = null

  /**
   * A mapping of themes to style definitions.
   */
  private var styleDefs: Map[String, String] = null

  /**
   * The widget used to display css style.
   */
  private var styleWidget: HTML = null

  /**
   * Whether the demo widget has been initialized.
   */
  private var widgetInitialized = false

  /**
   * Whether the demo widget is (asynchronously) initializing.
   */
  private var widgetInitializing = false

  /**
   * A vertical panel that holds the demo widget once it is initialized.
   */
  private var widgetVpanel: VerticalPanel = null

  /**
   * Add an item to this content widget. Should not be called before
   * {@link #onInitializeComplete} has been called.
   * 
   * @param w the widget to add
   * @param tabText the text to display in the tab
   */
  def add(w: Widget, tabText: String): Unit = {
    tabBar.addTab(tabText)
    deckPanel.add(w)
  }

  override def ensureWidget(): Unit = {
    super.ensureWidget()
    ensureWidgetInitialized(widgetVpanel)
  }

  /**
   * Get the description of this example.
   * 
   * @return a description for this example
   */
  def getDescription(): String

  /**
   * Get the name of this example to use as a title.
   * 
   * @return a name for this example
   */
  def getName(): String

  /**
   * @return the tab bar
   */
  def getTabBar() = tabBar

  /**
   * Returns true if this widget has a source section.
   * 
   * @return true if source tab available
   */
  def hasSource() = true

  /**
   * Returns true if this widget has a style section.
   * 
   * @return true if style tab available
   */
  def hasStyle() = true

  /**
   * When the widget is first initialized, this method is called. If it returns
   * a Widget, the widget will be added as the first tab. Return null to disable
   * the first tab.
   * 
   * @return the widget to add to the first tab
   */
  def onInitialize(): Widget

  /**
   * Called when initialization has completed and the widget has been added to
   * the page.
   */
  def onInitializeComplete(): Unit = {
  }

  def onSelection(event: SelectionEvent[java.lang.Integer]): Unit = {
    // Show the associated widget in the deck panel
    val tabIndex = event.getSelectedItem().intValue()
    deckPanel.showWidget(tabIndex)

    // Load the source code
    val tabHTML = getTabBar().getTabHTML(tabIndex)
    if (!sourceLoaded && tabHTML.equals(constants.contentWidgetSource())) {
      sourceLoaded = true
      var className = this.getClass().getName()
      className = className.substring(className.lastIndexOf(".") + 1)
      requestSourceContents(ShowcaseConstants.DST_SOURCE_EXAMPLE + className
          + ".html", sourceWidget, null)
    }

    // Load the style definitions
    if (hasStyle() && tabHTML.equals(constants.contentWidgetStyle())) {
      val theme = Showcase.CUR_THEME
      if (styleDefs.containsKey(theme)) {
        styleWidget.setHTML(styleDefs.get(theme))
      } else {
        styleDefs.put(theme, "")
        val callback = new RequestCallback() {
          def onError(request: Request,  exception: Throwable) {
            styleDefs.put(theme, "Style not available.")
          }

          def onResponseReceived(request: Request, response: Response) {
            styleDefs.put(theme, response.getText())
          }
        }

        var srcPath = ShowcaseConstants.DST_SOURCE_STYLE + theme
        if (LocaleInfo.getCurrentLocale().isRTL()) {
          srcPath += "_rtl"
        }
        var className = this.getClass().getName()
        className = className.substring(className.lastIndexOf(".") + 1)
        requestSourceContents(srcPath + "/" + className + ".html", styleWidget,
            callback)
      }
    }
  }

  /**
   * Select a tab.
   * 
   * @param index the tab index
   */
  def selectTab(index: Int) = tabBar.selectTab(index)

  protected def asyncOnInitialize(callback: AsyncCallback[Widget])

  /**
   * Initialize this widget by creating the elements that should be added to the
   * page.
   */
  @Override
  protected def createWidget(): Widget = {
    deckPanel = new DeckPanel()

    setStyleName(DEFAULT_STYLE_NAME)

    // Add a tab handler
    tabBar.addSelectionHandler(this)

    // Create a container for the main example
    widgetVpanel = new VerticalPanel()
    add(widgetVpanel, constants.contentWidgetExample())

    // Add the name
    val nameWidget = new HTML(getName())
    nameWidget.setStyleName(DEFAULT_STYLE_NAME + "-name")
    widgetVpanel.add(nameWidget)

    // Add the description
    val descWidget = new HTML(getDescription())
    descWidget.setStyleName(DEFAULT_STYLE_NAME + "-description")
    widgetVpanel.add(descWidget)

    // Add source code tab
    if (hasSource()) {
      sourceWidget = new HTML()
      add(sourceWidget, constants.contentWidgetSource())
    } else {
      sourceLoaded = true
    }

    // Add style tab
    if (hasStyle()) {
      styleDefs = new HashMap[String, String]()
      styleWidget = new HTML()
      add(styleWidget, constants.contentWidgetStyle())
    }

    deckPanel
  }

  override protected def onLoad(): Unit = {
    ensureWidget()

    // Select the first tab
    if (getTabBar().getTabCount() > 0) {
      tabBar.selectTab(0)
    }
  }

  /**
   * Load the contents of a remote file into the specified widget.
   * 
   * @param url a partial path relative to the module base URL
   * @param target the target Widget to place the contents
   * @param callback the callback when the call completes
   */
  protected def requestSourceContents(url: String, target: HTML, callback: RequestCallback) = {
    target.setDirection(DirectionHelper.getLtrDirection) // HasDirection.Direction.LTR
    DOM.setStyleAttribute(target.getElement(), "textAlign", "left")
    target.setHTML("&nbsp;&nbsp;" + ContentWidget.loadingImage)

    // Request the contents of the file
    val builder = new RequestBuilder(RequestBuilder.GET, GWT.getModuleBaseURL() + url)
    val realCallback = new RequestCallback() {
      def onError(request: Request, exception: Throwable) {
        target.setHTML("Cannot find resource")
        if (callback != null) {
          callback.onError(request, exception)
        }
      }

      def onResponseReceived(request: Request, response: Response) {
        target.setHTML(response.getText())
        Prettify.prettyPrint()
        if (callback != null) {
          callback.onResponseReceived(request, response)
        }
      }
    }
    builder.setCallback(realCallback)

    // Send the request
    try {
      builder.send()
    } catch {
      case e: RequestException => realCallback.onError(null, e)
    }
  }

  /**
   * Ensure that the demo widget has been initialized. Note that initialization
   * can fail if there is a network failure.
   */
  private def ensureWidgetInitialized(vPanel: VerticalPanel): Unit = {
    if (widgetInitializing || widgetInitialized) {
      return
    }

    widgetInitializing = true

    asyncOnInitialize(new AsyncCallback[Widget]() {
      def onFailure(reason: Throwable) {
        widgetInitializing = false
        Window.alert("Failed to download code for this widget (" + reason + ")")
      }

      def onSuccess(result: Widget) {
        widgetInitializing = false
        widgetInitialized = true

        val widget = result
        if (widget != null) {
          vPanel.add(widget)
        }
        onInitializeComplete()
      }
    })
  }
}
