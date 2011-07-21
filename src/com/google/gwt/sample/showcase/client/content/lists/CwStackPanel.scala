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
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.i18n.client.Constants
import com.google.gwt.resources.client.ImageResource
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Handlers._
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.AbstractImagePrototype
import com.google.gwt.user.client.ui.CheckBox
import com.google.gwt.user.client.ui.DecoratedStackPanel
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.HasVerticalAlignment
import com.google.gwt.user.client.ui.HorizontalPanel
import com.google.gwt.user.client.ui.Image
import com.google.gwt.user.client.ui.PopupPanel
import com.google.gwt.user.client.ui.Tree
import com.google.gwt.user.client.ui.TreeItem
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget
import com.google.gwt.resources.client.ClientBundle.Source

/**
 * Example file.
 */
object CwStackPanel {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwStackPanelFilters: Array[String]

    def cwStackPanelDescription: String

    def cwStackPanelMailFolders: Array[String]

    def cwStackPanelName: String

    def cwStackPanelFiltersHeader: String

    def cwStackPanelContactsHeader: String

    def cwStackPanelContacts: Array[String]

    def cwStackPanelContactsEmails: Array[String]

    def cwStackPanelMailHeader: String
  }

  /**
   * Specifies the images that will be bundled for this example.
   *
   * We will override the leaf image used in the tree. Instead of using a blank
   * 16x16 image, we will use a blank 1x1 image so it does not take up any
   * space. Each TreeItem will use its own custom image.
   */
  @ShowcaseSource
  trait Images extends Tree.Resources {
    def inbox: ImageResource

    def templates: ImageResource

    /**
     * Use noimage.png, which is a blank 1x1 image.
     */
    @Source(Array("noimage.png"))
    def treeLeaf: ImageResource

    def sent: ImageResource

    def trash: ImageResource

    def filtersgroup: ImageResource

    def contactsgroup: ImageResource

    def drafts: ImageResource

    def mailgroup: ImageResource

    def defaultContact: ImageResource
  }
}

@ShowcaseStyle(Array(
    ".gwt-DecoratedStackPanel", "html>body .gwt-DecoratedStackPanel",
    "* html .gwt-DecoratedStackPanel", ".cw-StackPanelHeader"))
class CwStackPanel(@ShowcaseData private val constants: CwStackPanel.CwConstants)
      extends ContentWidget(constants) {

  def getDescription: String = constants.cwStackPanelDescription
  def getName: String = constants.cwStackPanelName
  override def hasStyle = true

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  def onInitialize: Widget = {
    // Get the images
    val images = GWT.create(classOf[CwStackPanel.Images]).asInstanceOf[CwStackPanel.Images]
      
    // Create a new stack panel
    val stackPanel = new DecoratedStackPanel
    stackPanel.setWidth("200px")

    // Add the Mail folders
    val mailHeader = getHeaderString(constants.cwStackPanelMailHeader, images.mailgroup)
    stackPanel.add(createMailItem(images), mailHeader, true)

    // Add a list of filters
    val filtersHeader = getHeaderString(constants.cwStackPanelFiltersHeader, images.filtersgroup)
    stackPanel.add(createFiltersItem, filtersHeader, true)

    // Add a list of contacts
    val contactsHeader = getHeaderString(constants.cwStackPanelContactsHeader, images.contactsgroup)
    stackPanel.add(createContactsItem(images), contactsHeader, true)

    // Return the stack panel
    stackPanel.ensureDebugId("cwStackPanel")
    stackPanel
  }

  protected[client] def asyncOnInitialize(callback: AsyncCallback[Widget]): Unit = {
    GWT.runAsync(new RunAsyncCallback {
      def onFailure(caught: Throwable): Unit = {
        callback.onFailure(caught)
      }

      def onSuccess: Unit = {
        callback.onSuccess(onInitialize)
      }
    })
  }

  private def addItem(root: TreeItem,
                      image: ImageResource,
                      label: String): Unit = {
    root.addItem(AbstractImagePrototype.create(image).getHTML + " " + label)
  }

  /**
   * Create the list of Contacts.
   *
   * @param images the    { @link Images } used in the Contacts
   * @return the list of contacts
   */
  @ShowcaseSource
  private def createContactsItem(images: CwStackPanel.Images): VerticalPanel = {
    // Create a popup to show the contact info when a contact is clicked
    val contactPopupContainer = new HorizontalPanel
    contactPopupContainer.setSpacing(5)
    contactPopupContainer.add(new Image(images.defaultContact))
    val contactInfo = new HTML
    contactPopupContainer.add(contactInfo)
    val contactPopup = new PopupPanel(true, false)
    contactPopup.setWidget(contactPopupContainer)

    // Create the list of contacts
    val contactsPanel = new VerticalPanel
    contactsPanel.setSpacing(4)
    val contactNames = constants.cwStackPanelContacts
    val contactEmails = constants.cwStackPanelContactsEmails
    // TODO .toList below is a workaround for https://github.com/scalagwt/scalagwt-scala/issues/6
    contactNames.toList.zip(contactEmails) foreach { pair =>
      val (contactName, contactEmail) = pair
      val contactLink: HTML =
        new HTML("""<a href="javascript:undefined;">""" + contactName + "</a>")
      contactsPanel.add(contactLink)

      // Open the contact info popup when the user clicks a contact
      contactLink onClick {
        // Set the info about the contact
        contactInfo.setHTML(contactName + "<br><i>" + contactEmail + "</i>")

        // Show the popup of contact info
        val left = contactLink.getAbsoluteLeft + 14
        val top = contactLink.getAbsoluteTop + 14
        contactPopup.setPopupPosition(left, top)
        contactPopup.show
      }
    }
    contactsPanel
  }

  /**
   * Create the list of filters for the Filters item.
   *
   * @return the list of filters
   */
  @ShowcaseSource
  private def createFiltersItem: VerticalPanel = {
    val filtersPanel = new VerticalPanel
    filtersPanel.setSpacing(4)
    for (filter <- constants.cwStackPanelFilters) {
      filtersPanel.add(new CheckBox(filter))
    }
    filtersPanel
  }

  /**
   * Create the {@link Tree} of Mail options.
   *
   * @param images the {@link Images} used in the Mail options
   * @return the {@link Tree} of mail options
   */
  @ShowcaseSource
  private def createMailItem(images: CwStackPanel.Images): Tree = {
    var mailPanel = new Tree(images)
    var mailPanelRoot = mailPanel.addItem("foo@example.com")
    var mailFolders = constants.cwStackPanelMailFolders
    List(images.inbox, images.drafts, images.templates,
        images.sent, images.trash).zipWithIndex foreach { pair =>
      val (img, idx) = pair
      addItem(mailPanelRoot, img, mailFolders(idx))
    }
    mailPanelRoot.setState(true)
    mailPanel
  }

  /**
   * Get a string representation of the header that includes an image and some
   * text.
   *
   * @param text the header text
   * @param image the    { @link ImageResource } to add next to the header
   * @return the header as a string
   */
  @ShowcaseSource
  private def getHeaderString(text: String, image: ImageResource): String = {
    // Add the image and text to a horizontal panel
    val hPanel = new HorizontalPanel
    hPanel.setSpacing(0)
    hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE)
    hPanel.add(new Image(image))
    val headerText: HTML = new HTML(text)
    headerText.setStyleName("cw-StackPanelHeader")
    hPanel.add(headerText)

    // Return the HTML string for the panel
    hPanel.getElement.getString
  }
}

