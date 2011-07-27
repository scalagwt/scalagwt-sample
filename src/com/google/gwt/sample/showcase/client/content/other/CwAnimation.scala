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
package com.google.gwt.sample.showcase.client.content.other

import com.google.gwt.animation.client.Animation
import com.google.gwt.core.client.GWT
import com.google.gwt.core.client.RunAsyncCallback
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.event.dom.client.ClickHandler
import com.google.gwt.i18n.client.Constants
import com.google.gwt.i18n.client.Constants.DefaultStringValue
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.Handlers._
import com.google.gwt.sample.showcase.client.Showcase
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.user.client.rpc.AsyncCallback
import com.google.gwt.user.client.ui.AbsolutePanel
import com.google.gwt.user.client.ui.Button
import com.google.gwt.user.client.ui.DecoratorPanel
import com.google.gwt.user.client.ui.HTML
import com.google.gwt.user.client.ui.HasHorizontalAlignment
import com.google.gwt.user.client.ui.HorizontalPanel
import com.google.gwt.user.client.ui.Image
import com.google.gwt.user.client.ui.VerticalPanel
import com.google.gwt.user.client.ui.Widget

object CwAnimation {
  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  trait CwConstants extends Constants with ContentWidget.CwConstants {
    @DefaultStringValue("Cancel")
    def cwAnimationCancel(): String

    def cwAnimationDescription(): String

    def cwAnimationName(): String

    @DefaultStringValue("Animation Options")
    def cwAnimationOptions(): String

    @DefaultStringValue("Start")
    def cwAnimationStart(): String
  }
}

/**
 * Example file.
 */
class CwAnimation(constants: CwAnimation.CwConstants) extends ContentWidget(constants) {

  /**
   * A custom animation that moves a small image around a circle in an
   * {@link AbsolutePanel}.
   */
  @ShowcaseSource
  class CustomAnimation extends Animation {
    /**
     * The x-coordinate of the center of the circle.
     */
    private val centerX = 120

    /**
     * The y-coordinate of the center of the circle.
     */
    private val centerY = 120

    /**
     * The radius of the circle.
     */
    private val radius = 100

    // Set the start position of the widgets
    onComplete()

    override protected def onComplete() {
      super.onComplete()
      startButton.setEnabled(true)
      cancelButton.setEnabled(false)
    }

    override protected def onStart() {
      super.onStart()
      startButton.setEnabled(false)
      cancelButton.setEnabled(true)
    }

    override protected def onUpdate(progress: Double) {
      val radian = 2 * java.lang.Math.PI * progress
      updatePosition(animateeLeft, radian, 0)
      updatePosition(animateeBottom, radian, 0.5 * java.lang.Math.PI)
      updatePosition(animateeRight, radian, java.lang.Math.PI)
      updatePosition(animateeTop, radian, 1.5 * java.lang.Math.PI)
    }

    /**
     * Update the position of the widget, adding a rotational offset.
     * 
     * @param w the widget to move
     * @param radian the progress in radian
     * @param offset the offset in radian
     */
    private def updatePosition(w: Widget, _radian: Double, offset: Double) {
      val radian = _radian + offset
      val x = (radius * java.lang.Math.cos(radian) + centerX).toInt
      val y = (radius * java.lang.Math.sin(radian) + centerY).toInt
      absolutePanel.setWidgetPosition(w, x, y)
    }
  }


  /**
   * The absolute panel used in the example.
   */
  @ShowcaseData
  private var absolutePanel: AbsolutePanel = null

  /**
   * The widget that is being animated.
   */
  @ShowcaseData
  private var animateeBottom: Widget = null

  /**
   * The widget that is being animated.
   */
  @ShowcaseData
  private var animateeLeft: Widget = null

  /**
   * The widget that is being animated.
   */
  @ShowcaseData
  private var animateeRight: Widget = null

  /**
   * The widget that is being animated.
   */
  @ShowcaseData
  private var animateeTop: Widget = null

  /**
   * The instance of an animation.
   */
  @ShowcaseData
  private var animation: CustomAnimation = null

  /**
   * The {@link Button} used to cancel the {@link Animation}.
   */
  @ShowcaseData
  private var cancelButton: Button = null

  /**
   * The {@link Button} used to start the {@link Animation}.
   */
  @ShowcaseData
  private var startButton: Button = null

  override def getDescription() = constants.cwAnimationDescription

  override def getName() = constants.cwAnimationName

  override def hasStyle() = false

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  override def onInitialize(): Widget = {
    // Create a new panel
    absolutePanel = new AbsolutePanel()
    absolutePanel.setSize("250px", "250px")
    absolutePanel.ensureDebugId("cwAbsolutePanel")

    // Add a widget that we will animate
    animateeTop = new Image(Showcase.images.gwtLogoThumb)
    animateeBottom = new Image(Showcase.images.gwtLogoThumb)
    animateeLeft = new Image(Showcase.images.gwtLogoThumb)
    animateeRight = new Image(Showcase.images.gwtLogoThumb)
    absolutePanel.add(animateeTop)
    absolutePanel.add(animateeBottom)
    absolutePanel.add(animateeLeft)
    absolutePanel.add(animateeRight)

    // Wrap the absolute panel in a DecoratorPanel
    val absolutePanelWrapper = new DecoratorPanel()
    absolutePanelWrapper.setWidget(absolutePanel)

    // Create the options bar
    val optionsWrapper = new DecoratorPanel()
    optionsWrapper.setWidget(createOptionsBar())

    // Add the components to a panel and return it
    val mainLayout = new HorizontalPanel()
    mainLayout.setSpacing(10)
    mainLayout.add(optionsWrapper)
    mainLayout.add(absolutePanelWrapper)

    // Create the custom animation
    animation = new CustomAnimation()

    // Return the layout
    return mainLayout
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
    val optionsBar = new VerticalPanel()
    optionsBar.setSpacing(5)
    optionsBar.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER)

    // Add a title
    optionsBar.add(new HTML("<b>" + constants.cwAnimationOptions + "</b>"))

    // Add start button
    startButton = new Button(constants.cwAnimationStart)
    startButton.addStyleName("sc-FixedWidthButton")
    startButton onClick { _ => animation.run(2000) }
    optionsBar.add(startButton)

    // Add cancel button
    cancelButton = new Button(constants.cwAnimationCancel)
    cancelButton.addStyleName("sc-FixedWidthButton")
    cancelButton onClick { _ => animation.cancel() }
    optionsBar.add(cancelButton)

    // Return the options bar
    return optionsBar
  }

}
