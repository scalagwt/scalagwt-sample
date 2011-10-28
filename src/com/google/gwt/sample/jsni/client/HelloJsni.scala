package com.google.gwt.sample.jsni.client

//import scala.util.nativeCode

import com.google.gwt.core.client.EntryPoint
import com.google.gwt.core.client.JavaScriptObject
import com.google.gwt.core.client.JsArrayInteger
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.event.dom.client.ClickHandler
import com.google.gwt.user.client.ui.Button
import com.google.gwt.user.client.Window
import com.google.gwt.user.client.ui.RootPanel

object HelloJsni {
  @native def getMessage(x: Int, y: Int): Point = nativeCode { """
    return [x, y];
  """ }
}

class HelloJsni extends EntryPoint {
  
  def onModuleLoad() {
    val numbers = getList(10)
    val s = numbers join ", "
    val p = getMessage(1, 2)
    val p2 = HelloJsni.getMessage(3, 4)
    //val p3 = Point.create(5, 6) // doesn't work in hosted mode
    val b = new Button("Click me", (_: ClickEvent) => Window.alert("Hello, AJAX, said Scala\nThose numbers are coming from jsni!\n" + s))
    val b2 = new Button("Click me", (_: ClickEvent) => Window.alert("Point: x=" + p.x + ", y=" + p.y))
    val b3 = new Button("Click me", (_: ClickEvent) => Window.alert("Point: x=" + p2.x + ", y=" + p2.y))
    //val b4 = new Button("Click me", (_: ClickEvent) => Window.alert("Point: x=" + p3.x + ", y=" + p3.y))
    RootPanel.get().add(b)
    RootPanel.get().add(b2)
    RootPanel.get().add(b3)
    //RootPanel.get().add(b4)
  }
  
  implicit def clickHandler(f: ClickEvent => Unit): ClickHandler = new ClickHandler {
    def onClick(event: ClickEvent) = f(event)
  }
  
  @native def getMessage(x: Int, y: Int): Point = nativeCode { """
    return [x, y];
  """ }
  
  @native def getList(len: Int): JsArrayInteger = nativeCode { """
    var arr = [];
    for (var i = 0; i < len; i++) {
      arr.push(i);
    }
    return arr;
  """ }
}

object Point {
  @native def create(x: Int, y: Int): Point = nativeCode { """
    return [x, y];
  """ }
}

class Point protected() extends JavaScriptObject {
  @native def x: Int = nativeCode("return this[0];")
  @native def y: Int = nativeCode("return this[1];")
}
