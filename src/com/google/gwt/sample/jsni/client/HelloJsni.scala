package com.google.gwt.sample.jsni.client;

//import scala.util.nativeCode;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

class HelloJsni extends EntryPoint {

  def onModuleLoad() {
    val numbers = getList
    val s = numbers join ", "
    val b = new Button("Click me", (_: ClickEvent) => Window.alert("Hello, AJAX, said Scala\nThose numbers are coming from jsni!\n" + s));
    RootPanel.get().add(b);
  }
  
  implicit def clickHandler(f: ClickEvent => Unit): ClickHandler = new ClickHandler {
    def onClick(event: ClickEvent) = f(event)
  }
  
  @native def nativeCode(s: String): Nothing = sys.error("nativeCode implementation missing!!!")
  
  def getList: JsArrayInteger = nativeCode { """
    return [1, 2, 3, 4];
  """ }
}
