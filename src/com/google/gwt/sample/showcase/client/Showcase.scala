package com.google.gwt.sample.showcase.client

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

import com.google.gwt.core.client.EntryPoint
import com.google.gwt.core.client.GWT
import com.google.gwt.dom.client.Element
import com.google.gwt.dom.client.HeadElement
import com.google.gwt.dom.client.Node
import com.google.gwt.dom.client.NodeList
import com.google.gwt.event.dom.client.ChangeEvent
import com.google.gwt.event.dom.client.ChangeHandler
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.event.dom.client.ClickHandler
import com.google.gwt.event.logical.shared.SelectionEvent
import com.google.gwt.event.logical.shared.SelectionHandler
import com.google.gwt.event.logical.shared.ValueChangeEvent
import com.google.gwt.event.logical.shared.ValueChangeHandler
import com.google.gwt.http.client.UrlBuilder
import com.google.gwt.i18n.client.LocaleInfo
import com.google.gwt.resources.client.ImageResource
import com.google.gwt.sample.showcase.client.content.i18n.CwConstantsExample
import com.google.gwt.sample.showcase.client.content.i18n.CwConstantsWithLookupExample
import com.google.gwt.sample.showcase.client.content.i18n.CwDateTimeFormat
import com.google.gwt.sample.showcase.client.content.i18n.CwDictionaryExample
import com.google.gwt.sample.showcase.client.content.i18n.CwMessagesExample
import com.google.gwt.sample.showcase.client.content.i18n.CwNumberFormat
import com.google.gwt.sample.showcase.client.content.i18n.CwPluralFormsExample
import com.google.gwt.sample.showcase.client.content.lists.CwListBox
import com.google.gwt.sample.showcase.client.content.lists.CwMenuBar
import com.google.gwt.sample.showcase.client.content.lists.CwStackPanel
import com.google.gwt.sample.showcase.client.content.lists.CwSuggestBox
import com.google.gwt.sample.showcase.client.content.lists.CwTree
import com.google.gwt.sample.showcase.client.content.other.CwAnimation
import com.google.gwt.sample.showcase.client.content.other.CwCookies
import com.google.gwt.sample.showcase.client.content.panels.CwAbsolutePanel
import com.google.gwt.sample.showcase.client.content.panels.CwDecoratorPanel
import com.google.gwt.sample.showcase.client.content.panels.CwDisclosurePanel
import com.google.gwt.sample.showcase.client.content.panels.CwDockPanel
import com.google.gwt.sample.showcase.client.content.panels.CwFlowPanel
import com.google.gwt.sample.showcase.client.content.panels.CwHorizontalPanel
import com.google.gwt.sample.showcase.client.content.panels.CwHorizontalSplitPanel
import com.google.gwt.sample.showcase.client.content.panels.CwTabPanel
import com.google.gwt.sample.showcase.client.content.panels.CwVerticalPanel
import com.google.gwt.sample.showcase.client.content.panels.CwVerticalSplitPanel
import com.google.gwt.sample.showcase.client.content.popups.CwBasicPopup
import com.google.gwt.sample.showcase.client.content.popups.CwDialogBox
import com.google.gwt.sample.showcase.client.content.tables.CwFlexTable
import com.google.gwt.sample.showcase.client.content.tables.CwGrid
import com.google.gwt.sample.showcase.client.content.text.CwBasicText
import com.google.gwt.sample.showcase.client.content.text.CwRichText
import com.google.gwt.sample.showcase.client.content.widgets.CwBasicButton
import com.google.gwt.sample.showcase.client.content.widgets.CwCheckBox
import com.google.gwt.sample.showcase.client.content.widgets.CwCustomButton
import com.google.gwt.sample.showcase.client.content.widgets.CwDatePicker
import com.google.gwt.sample.showcase.client.content.widgets.CwFileUpload
import com.google.gwt.sample.showcase.client.content.widgets.CwHyperlink
import com.google.gwt.sample.showcase.client.content.widgets.CwRadioButton
import com.google.gwt.user.client.Command
import com.google.gwt.user.client.History
import com.google.gwt.user.client.Window
import com.google.gwt.user.client.Window.Location
import com.google.gwt.user.client.ui.AbstractImagePrototype
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.HasHorizontalAlignment
import com.google.gwt.user.client.ui.HasVerticalAlignment
import com.google.gwt.user.client.ui.HorizontalPanel
import com.google.gwt.user.client.ui.Image
import com.google.gwt.user.client.ui.ListBox
import com.google.gwt.user.client.ui.RootPanel
import com.google.gwt.user.client.ui.TabBar
import com.google.gwt.user.client.ui.ToggleButton
import com.google.gwt.user.client.ui.Tree
import com.google.gwt.user.client.ui.TreeItem
import com.google.gwt.user.client.ui.VerticalPanel
import java.util.ArrayList
import java.util.HashMap
import java.util.Map

object Showcase {
   /**
    * A special version of the ToggleButton that cannot be clicked if down. If
    * one theme button is pressed, all of the others are depressed.
    */
   private object ThemeButton {
      private var allButtons: List[ThemeButton] = null
   }
   private class ThemeButton(val theme: String) extends ToggleButton {
      import ThemeButton._

      addStyleName("sc-ThemeButton-" + theme)

      // Add this button to the static list
      if (allButtons == null) {
         allButtons = Nil;
         setDown(true)
      }
      allButtons = this :: allButtons

      protected override def onClick: Unit = {
         if (!isDown) {
            // Raise all of the other buttons
            allButtons filterNot (_ == this) foreach {
               _.setDown(false);
            }

            // Fire the click handlers
            super.onClick
         }
      }
   }

   /**
    * The static images used throughout the Showcase.
    */
   val images: ShowcaseImages = GWT.create(classOf[ShowcaseImages]).asInstanceOf[ShowcaseImages]

   /**
    * The current style theme.
    */
   private[client] var CUR_THEME: String = ShowcaseConstants.STYLE_THEMES(0)

   /**
    * The type passed into the
    * { @link com.google.gwt.sample.showcase.generator.ShowcaseGenerator }.
    */
   private final class GeneratorInfo
}

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
class Showcase extends EntryPoint {
   import Showcase._
   import Handlers._

   /**
    * The {@link Application}.
    */
   private var app = new Application

   /**
    * A mapping of history tokens to their associated menu items.
    */
   private var itemTokens: Map[String, TreeItem] = new HashMap

   /**
    * A mapping of menu items to the widget display when the item is selected.
    */
   private var itemWidgets: Map[TreeItem, ContentWidget] = new HashMap

   /**
    * This is the entry point method.
    */
   def onModuleLoad: Unit = {
      // Generate the source code and css for the examples
      GWT.create(classOf[GeneratorInfo])

      // Create the constants
      val constants = GWT.create(classOf[ShowcaseConstants]).asInstanceOf[ShowcaseConstants]

      // Swap out the style sheets for the RTL versions if needed.
      updateStyleSheets;

      // Create the application
      setupTitlePanel(constants)
      setupMainLinks(constants)
      setupOptionsPanel
      setupMainMenu(constants)

      // Setup a history handler to reselect the associate menu item
      History.addValueChangeHandler { event: ValueChangeEvent[String] =>
         var item: TreeItem = itemTokens.get(event.getValue)
         if (item == null) {
            item = app.getMainMenu.getItem(0).getChild(0)
         }
         app.getMainMenu.setSelectedItem(item, false)
         app.getMainMenu.ensureSelectedItemVisible
         displayContentWidget(itemWidgets.get(item))
      }

      // Add a handler that sets the content widget when a menu item is selected
      app.addSelectionHandler { event: SelectionEvent[TreeItem] =>
         var item = event.getSelectedItem
         var content: ContentWidget = itemWidgets.get(item)
         if (content != null && !content.equals(app.getContent)) {
            History.newItem(getContentWidgetToken(content))
         }
      }

      // Show the initial example
      if (History.getToken.length > 0) {
         History.fireCurrentHistoryState
      } else {
         // Use the first token available
         val firstItem: TreeItem = app.getMainMenu.getItem(0).getChild(0)
         app.getMainMenu.setSelectedItem(firstItem, false)
         app.getMainMenu.ensureSelectedItemVisible
         displayContentWidget(itemWidgets.get(firstItem))
      }
   }

   /**
    * Set the content to the    { @link ContentWidget }.
    *
    * @param content the    { @link ContentWidget } to display
    */
   private def displayContentWidget(content: ContentWidget): Unit = {
      if (content != null) {
         app.setContent(content)
         app.setContentTitle(content.getTabBar)
      }
   }

   /**
    * Get the token for a given content widget.
    *
    * @return the content widget token.
    */
   private def getContentWidgetToken(content: ContentWidget): String = {
      var className = content.getClass.getName
      className = className.substring(className.lastIndexOf('.') + 1)
      return className
   }

   /**
    * Get the style name of the reference element defined in the current GWT
    * theme style sheet.
    *
    * @param prefix the prefix of the reference style name
    * @return the style name
    */
   private def getCurrentReferenceStyleName(prefix: String): String = {
      var gwtRef = prefix + "-Reference-" + CUR_THEME
      if (LocaleInfo.getCurrentLocale.isRTL) {
         gwtRef += "-rtl"
      }
      return gwtRef
   }

   /**
    * Create the main links at the top of the application.
    *
    * @param constants the constants with text
    */
   private def setupMainLinks(constants: ShowcaseConstants): Unit = {
      // Link to GWT Homepage
      app.addLink(new HTML("<a href=" + ShowcaseConstants.GWT_HOMEPAGE + ">" + constants.mainLinkHomepage + "</a>"))

      // Link to More Examples
      app.addLink(new HTML("<a href=" + ShowcaseConstants.GWT_EXAMPLES + ">" + constants.mainLinkExamples + "</a>"))
   }

   /**
    * Setup all of the options in the main menu.
    *
    * @param constants the constant values to use
    */
   private def setupMainMenu(constants: ShowcaseConstants): Unit = {
      val mainMenu = app.getMainMenu

      // Widgets
      val catWidgets = mainMenu.addItem(constants.categoryWidgets)
      List(new CwCheckBox(constants), new CwRadioButton(constants),
           new CwBasicButton(constants), new CwCustomButton(constants),
           new CwFileUpload(constants), new CwDatePicker(constants),
           new CwHyperlink(constants)) foreach {
         setupMainMenuOption(catWidgets, _, images.catWidgets);
      }

      // Lists
      val catLists = mainMenu.addItem(constants.categoryLists)
      List(new CwListBox(constants), new CwSuggestBox(constants),
           new CwTree(constants), new CwMenuBar(constants),
           new CwStackPanel(constants)) foreach {
         setupMainMenuOption(catLists, _, images.catLists)
      }

      // Text
      val catText = mainMenu.addItem(constants.categoryTextInput)
      List(new CwBasicText(constants), new CwRichText(constants)) foreach {
         setupMainMenuOption(catText, _, images.catTextInput)
      }

      // Popups
      val catPopup = mainMenu.addItem(constants.categoryPopups)
      List(new CwBasicPopup(constants), new CwDialogBox(constants)) foreach {
         setupMainMenuOption(catPopup, _, images.catPopups)
      }

      // Panels
      val catPanels = mainMenu.addItem(constants.categoryPanels)
      List(new CwDecoratorPanel(constants), new CwFlowPanel(constants),
           new CwHorizontalPanel(constants), new CwVerticalPanel(constants),
           new CwAbsolutePanel(constants), new CwDockPanel(constants),
           new CwDisclosurePanel(constants), new CwTabPanel(constants),
           new CwHorizontalSplitPanel(constants),
           new CwVerticalSplitPanel(constants)) foreach {
         setupMainMenuOption(catPanels, _, images.catPanels)
      }

      // Tables
      val catTables = mainMenu.addItem(constants.categoryTables)
      List(new CwGrid(constants), new CwFlexTable(constants)) foreach {
         setupMainMenuOption(catTables, _, images.catTables)
      }

      // Internationalization
      val catI18N = mainMenu.addItem(constants.categoryI18N)
      List(new CwNumberFormat(constants), new CwDateTimeFormat(constants),
           new CwMessagesExample(constants), new CwPluralFormsExample(constants),
           new CwConstantsExample(constants),
           new CwConstantsWithLookupExample(constants),
           new CwDictionaryExample(constants)) foreach {
         setupMainMenuOption(catI18N, _, images.catI18N)
      }

      // Other
      val catOther = mainMenu.addItem(constants.categoryOther)
      List(new CwAnimation(constants), new CwCookies(constants)) foreach {
         setupMainMenuOption(catOther, _, images.catOther)
      }
   }

   /**
    * Add an option to the main menu.
    *
    * @param parent the    { @link TreeItem } that is the option
    * @param content the    { @link ContentWidget } to display when selected
    * @param image the icon to display next to the    { @link TreeItem }
    */
   private def setupMainMenuOption(parent: TreeItem, content: ContentWidget, image: ImageResource): Unit = {
      // Create the TreeItem
      val option = parent.addItem(AbstractImagePrototype.create(image).getHTML + " " + content.getName)

      // Map the item to its history token and content widget
      itemWidgets.put(option, content)
      itemTokens.put(getContentWidgetToken(content), option)
   }

   /**
    * Create the options that appear next to the title.
    */
   private def setupOptionsPanel: Unit = {
      val vPanel = new VerticalPanel
      vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT)
      if (LocaleInfo.getCurrentLocale.isRTL) {
         vPanel.getElement.setAttribute("align", "left")
      } else {
         vPanel.getElement.setAttribute("align", "right")
      }
      app.setOptionsWidget(vPanel)

      // Add the option to change the locale
      val localeBox = new ListBox
      var currentLocale = LocaleInfo.getCurrentLocale.getLocaleName
      if (currentLocale.equals("default")) {
         currentLocale = "en"
      }
      val localeNames = LocaleInfo.getAvailableLocaleNames
      for (localeName <- localeNames) {
         if (!localeName.equals("default")) {
            val nativeName = LocaleInfo.getLocaleNativeDisplayName(localeName)
            localeBox.addItem(nativeName, localeName)
            if (localeName.equals(currentLocale)) {
               localeBox.setSelectedIndex(localeBox.getItemCount - 1)
            }
         }
      }
      localeBox.addChangeHandler { event: ChangeEvent =>
         val localeName = localeBox.getValue(localeBox.getSelectedIndex)
         val builder = Location.createUrlBuilder.setParameter("locale", localeName)
         Window.Location.replace(builder.buildString)
      }
      val localeWrapper = new HorizontalPanel
      localeWrapper.add(new Image(images.locale))
      localeWrapper.add(localeBox)
      vPanel.add(localeWrapper)

      // Add the option to change the style
      val styleWrapper = new HorizontalPanel
      vPanel.add(styleWrapper)

      ShowcaseConstants.STYLE_THEMES foreach { theme =>
         val button: ThemeButton = new ThemeButton(theme)
         styleWrapper.add(button)
         button.addClickHandler { event: ClickEvent =>
            // Update the current theme
            CUR_THEME = button.theme

            // Reload the current tab, loading the new theme if necessary
            val bar = (app.getContentTitle.asInstanceOf[TabBar])
            bar.selectTab(bar.getSelectedTab)

            // Load the new style sheets
            updateStyleSheets
         }
      }
   }

   /**
    * Create the title bar at the top of the application.
    *
    * @param constants the constant values to use
    */
   private def setupTitlePanel(constants: ShowcaseConstants): Unit = {
      // Get the title from the internationalized constants
      val pageTitle = "<h1>" + constants.mainTitle + "</h1><h2>" + constants.mainSubTitle + "</h2>";

      // Add the title and some images to the title bar
      val titlePanel = new HorizontalPanel
      titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE)
      titlePanel.add(new Image(images.gwtLogo))
      titlePanel.add(new HTML(pageTitle.toString))
      app.setTitleWidget(titlePanel)
   }

   /**
    * Update the style sheets to reflect the current theme and direction.
    */
   private def updateStyleSheets: Unit = {
      // Generate the names of the style sheets to include
      var gwtStyleSheet = "gwt/" + CUR_THEME + "/" + CUR_THEME + ".css"
      var showcaseStyleSheet = CUR_THEME + "/Showcase.css"
      if (LocaleInfo.getCurrentLocale.isRTL) {
         gwtStyleSheet = gwtStyleSheet.replace(".css", "_rtl.css")
         showcaseStyleSheet = showcaseStyleSheet.replace(".css", "_rtl.css")
      }

      // Find existing style sheets that need to be removed
      var styleSheetsFound = false
      val headElem: HeadElement = DomUtil.getHeadElement
      var toRemove: List[Element] = Nil
      /* TODO compare to GWT trunk
      val children: NodeList[Node] = headElem.getChildNodes
      for (i <- 0 to children.getLength;
           node = children.getItem(i) if (node.getNodeType == Node.ELEMENT_NODE);
           elem = Element.as(node);
           if (elem.getTagName.equalsIgnoreCase("link") &&
                 elem.getPropertyString("rel").equalsIgnoreCase("stylesheet"))) {
         styleSheetsFound = true
         val href = elem.getPropertyString("href");
         // If the correct style sheets are already loaded, then we should have
         // nothing to remove.
         if (!href.contains(gwtStyleSheet) && !href.contains(showcaseStyleSheet)) {
            toRemove = elem :: toRemove;
         }
      }
      */

      // Return if we already have the correct style sheets
      if (styleSheetsFound && toRemove.size == 0) {
         return
      }

      // Detach the app while we manipulate the styles to avoid rendering issues
      RootPanel.get.remove(app)

      // Remove the old style sheets
      for (elem <- toRemove) {
         headElem.removeChild(elem)
      }

      // Load the GWT theme style sheet
      val modulePath = GWT.getModuleBaseURL
      val callback = new Command {
         /**
          * The number of style sheets that have been loaded and executed this
          * command.
          */
         private var numStyleSheetsLoaded: Int = 0

         def execute: Unit = {
            // Wait until all style sheets have loaded before re-attaching the app
            numStyleSheetsLoaded += 1;
            if (numStyleSheetsLoaded < 2) {
               return
            }

            // Different themes use different background colors for the body
            // element, but IE only changes the background of the visible content
            // on the page instead of changing the background color of the entire
            // page. By changing the display style on the body element, we force
            // IE to redraw the background correctly.
            RootPanel.getBodyElement.getStyle.setProperty("display", "none")
            RootPanel.getBodyElement.getStyle.setProperty("display", "")
            RootPanel.get.add(app)
         }
      }
      StyleSheetLoader.loadStyleSheet(modulePath + gwtStyleSheet,
                                      getCurrentReferenceStyleName("gwt"), callback)

      // Load the showcase specific style sheet after the GWT theme style sheet so
	   // that custom styles supercede the theme styles.
      StyleSheetLoader.loadStyleSheet(modulePath + showcaseStyleSheet,
                                      getCurrentReferenceStyleName("Application"), callback)
   }
}


