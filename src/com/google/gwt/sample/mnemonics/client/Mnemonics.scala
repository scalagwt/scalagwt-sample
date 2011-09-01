package com.google.gwt.sample.mnemonics.client

import com.google.gwt.core.client.EntryPoint
import com.google.gwt.event.dom.client.KeyUpHandler
import com.google.gwt.event.dom.client.KeyUpEvent
import com.google.gwt.user.client.ui.Button
import com.google.gwt.user.client.Window
import com.google.gwt.user.client.ui._
import com.google.gwt.user.client.DOM
import com.google.gwt.dom.client.Document

import scala.collection.JavaConversions._

/**
  * Handles page updates. This code is not an example of GWT idiomatic code but rather a quick
  * hack.
  */
class Mnemonics extends EntryPoint {
  
  val dict = List("Scala", "rocks", "Pack", "brocks", "GWT", "implicit", "nice", "ScalaGWT", "cat", "EFPL", "Lausanne")
  val coder = new Coder(dict)

  def onModuleLoad() {
    val l = new Label
    val t = new TextBox
    val mainPanel = RootPanel.get("main")
    val inputPanel = new FlowPanel
    inputPanel.setStyleName("input")
    t.addStyleName("xlarge")
    t.getElement().setId("phonenumber")
    inputPanel.add(t)
    val outputPanel = new FlowPanel
    outputPanel.add(l)
    l.addStyleName("well")
    l.addStyleName("output")
    t addKeyUpHandler new KeyUpHandler {
      def onKeyUp(event: KeyUpEvent) = {
        val mnemonics = coder.translate(t.getText())
        if (mnemonics contains "implicit cat")
          Document.get().getElementById("implicitcat").setAttribute("class", "modal")
        val output = new HTMLPanel(if (mnemonics.isEmpty) "<i>No mnemonics found<i/>" else mnemonics mkString "<br/>")
        output.addStyleName("well")
        output.addStyleName("output")
        outputPanel.iterator().foreach(outputPanel.remove(_))
        outputPanel.add(output)
      }
    }
    mainPanel.add(inputPanel)
    mainPanel.add(outputPanel)
    addSuggestions(mainPanel)
    Prettify.prettyPrint()
    t.setFocus(true)
  }
  
  def addSuggestions(panel: Panel) {
    val h4 = new HTML("<h4>Try one of those</h4>")
    panel.add(h4)
    val suggestions = {
      val random = new util.Random(122)
      random.shuffle(dict).sliding(2).map(_.map(coder.wordCode) mkString "").toList
    }
    val label = new Label((suggestions take 7) mkString ", ")
    label.setStyleName("well")
    panel.add(label)
  }

}
