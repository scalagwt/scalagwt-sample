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
import com.google.gwt.event.logical.shared.OpenEvent
import com.google.gwt.i18n.client.Constants
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Handlers._
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle
import com.google.gwt.user.client.Random
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.DecoratorPanel
import com.google.gwt.user.client.ui.Grid
import com.google.gwt.user.client.ui.HasVerticalAlignment
import com.google.gwt.user.client.ui.ScrollPanel
import com.google.gwt.user.client.ui.Tree
import com.google.gwt.user.client.ui.TreeItem
import com.google.gwt.user.client.ui.Widget

/**
 * Example file.
 */
object CwTree {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    def cwTreeMozartWorkConcertos: Array[String]

    def cwTreeBrahmsWorkSymphonies: Array[String]

    def cwTreeConcertos: String

    def cwTreeItem: String

    def cwTreeComposers: Array[String]

    def cwTreeBeethovenWorkSonatas: Array[String]

    def cwTreeSymphonies: String

    def cwTreeName: String

    def cwTreeQuartets: String

    def cwTreeBrahmsWorkQuartets: Array[String]

    def cwTreeBrahmsWorkConcertos: Array[String]

    def cwTreeBrahmsWorkSonatas: Array[String]

    def cwTreeBeethovenWorkQuartets: Array[String]

    def cwTreeDynamicLabel: String

    def cwTreeBeethovenWorkSymphonies: Array[String]

    def cwTreeStaticLabel: String

    def cwTreeDescription: String

    def cwTreeBeethovenWorkConcertos: Array[String]

    def cwTreeSonatas: String
  }
}
@ShowcaseStyle(Array(".gwt-Tree"))
class CwTree(@ShowcaseData private val constants: CwTree.CwConstants)
      extends ContentWidget(constants) {

  def getDescription: String = constants.cwTreeDescription
  def getName: String = constants.cwTreeName

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  def onInitialize: Widget = {
    // Create a static tree and a container to hold it
    val staticTree = createStaticTree
    staticTree.setAnimationEnabled(true)
    staticTree.ensureDebugId("cwTree-staticTree")
    val staticTreeWrapper = new ScrollPanel(staticTree)
    staticTreeWrapper.ensureDebugId("cwTree-staticTree-Wrapper")
    staticTreeWrapper.setSize("300px", "300px")

    // Wrap the static tree in a DecoratorPanel
    val staticDecorator = new DecoratorPanel
    staticDecorator.setWidget(staticTreeWrapper)

    // Create a dynamically generated tree and a container to hold it
    val dynamicTree = createDynamicTree
    dynamicTree.ensureDebugId("cwTree-dynamicTree")
    val dynamicTreeWrapper = new ScrollPanel(dynamicTree)
    dynamicTreeWrapper.ensureDebugId("cwTree-dynamicTree-Wrapper")
    dynamicTreeWrapper.setSize("300px", "300px")

    // Wrap the dynamic tree in a DecoratorPanel
    val dynamicDecorator = new DecoratorPanel
    dynamicDecorator.setWidget(dynamicTreeWrapper)

    // Combine trees onto the page
    val grid = new Grid(2, 3)
    grid.setCellPadding(2)
    grid.getRowFormatter.setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP)
    grid.setHTML(0, 0, constants.cwTreeStaticLabel)
    grid.setHTML(0, 1, "&nbsp;&nbsp;&nbsp;")
    grid.setHTML(0, 2, constants.cwTreeDynamicLabel)
    grid.setWidget(1, 0, staticDecorator)
    grid.setHTML(1, 1, "&nbsp;&nbsp;&nbsp;")
    grid.setWidget(1, 2, dynamicDecorator)

    // Wrap the trees in DecoratorPanels
    grid
  }

  protected[client] def asyncOnInitialize(callback: AsyncCallback[Widget]): Unit = {
    GWT.runAsync(new RunAsyncCallback {
      def onSuccess: Unit = {
        callback.onSuccess(onInitialize)
      }

      def onFailure(caught: Throwable): Unit = {
        callback.onFailure(caught)
      }
    })
  }

  /**
   * Add a new section of music created by a specific composer.
   *
   * @param parent the parent   { @link TreeItem } where the section will be added
   * @param label the label of the new section of music
   * @param composerWorks an array of works created by the composer
   */
  @ShowcaseSource
  private def addMusicSection(parent: TreeItem) = { (label: String, composerWorks: Array[String]) =>
    val section = parent.addItem(label)
    for (work <- composerWorks) {
      section.addItem(work)
    }
  }

  /**
   * Create a dynamic tree that will add a random number of children to each
   * node as it is clicked.
   *
   * @return the new tree
   */
  @ShowcaseSource
  private def createDynamicTree: Tree = {
    // Create a new tree
    val dynamicTree = new Tree

    // Add some default tree items
    for (i <- 0 until 5) {
      val item = dynamicTree.addItem(constants.cwTreeItem + " " + i)

      // Temporarily add an item so we can expand this node
      item.addItem("")
    }

    // Add a handler that automatically generates some children
    dynamicTree onOpen { event =>
      val item = event.getTarget
      if (item.getChildCount == 1) {
        // Close the item immediately
        item.setState(false, false)

        // Add a random number of children to the item
        val itemText = item.getText
        val numChildren = Random.nextInt(5) + 2
        for (i <- 0 until numChildren) {
          val child = item.addItem(itemText + "." + i)
          child.addItem("")
        }

        // Remove the temporary item when we finish loading
        item.getChild(0).remove

        // Reopen the item
        item.setState(true, false)
      }
    }

    // Return the tree
    dynamicTree
  }

  /**
   * Create a static tree with some data in it.
   *
   * @return the new tree
   */
  @ShowcaseSource
  private def createStaticTree: Tree = {
    // Create the tree
    val composers = constants.cwTreeComposers
    val concertosLabel = constants.cwTreeConcertos
    val quartetsLabel = constants.cwTreeQuartets
    val sonatasLabel = constants.cwTreeSonatas
    val symphoniesLabel = constants.cwTreeSymphonies
    val staticTree = new Tree

    // Add some of Beethoven's music
    val beethovenItem = staticTree.addItem(composers(0))
    List(concertosLabel -> constants.cwTreeBeethovenWorkConcertos,
         quartetsLabel -> constants.cwTreeBeethovenWorkQuartets,
         sonatasLabel -> constants.cwTreeBeethovenWorkSonatas,
         symphoniesLabel -> constants.cwTreeBeethovenWorkSymphonies) foreach addMusicSection(beethovenItem).tupled

    // Add some of Brahms's music
    val brahmsItem = staticTree.addItem(composers(1))
    List(concertosLabel -> constants.cwTreeBrahmsWorkConcertos,
         quartetsLabel -> constants.cwTreeBrahmsWorkQuartets,
         sonatasLabel -> constants.cwTreeBrahmsWorkSonatas,
         symphoniesLabel -> constants.cwTreeBrahmsWorkSymphonies) foreach addMusicSection(brahmsItem).tupled

    // Add some of Mozart's music
    val mozartItem = staticTree.addItem(composers(2))
    addMusicSection(mozartItem)(concertosLabel, constants.cwTreeMozartWorkConcertos)

    // Return the tree
    staticTree
  }

}

