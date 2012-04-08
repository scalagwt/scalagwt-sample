package com.google.gwt.sample.jsni.client

//import scala.util.nativeCode

import com.google.gwt.core.client.EntryPoint
import com.google.gwt.core.client.JavaScriptObject
import com.google.gwt.core.client.JsArrayInteger
import com.google.gwt.core.client.SingleJsoImpl
import com.google.gwt.event.dom.client.ClickEvent
import com.google.gwt.event.dom.client.ClickHandler
import com.google.gwt.user.client.ui.Button
import com.google.gwt.user.client.Window
import com.google.gwt.user.client.ui.RootPanel

object util {
  def nativeCode(body: String) = sys.error("nativeCode: method body not provided")
}

import util._

object HelloJsni {
  def apply(x: Int, y: Int): Point = getMessage(x, y)
  
  @native def getMessage(x: Int, y: Int): Point = nativeCode { """
    return [x, y];
  """ }
}

class HelloJsni extends EntryPoint {
  
  def onModuleLoad() {
    val numbers = getList(10)
    val s = numbers.join(", ")
    val p2 = getMessage(1, 2)
    val p3 = HelloJsni.getMessage(3, 4)
    val p4 = HelloJsni(5, 6)
    val p5 = Point.create(7, 8)
    val p6 = Point(9, 10)
    
    val Point(x, y) = HelloJsni.getMessage(3, 4)
    
    val b = new Button("Click me", (_: ClickEvent) => Window.alert("Hello, AJAX, said Scala\nThose numbers are coming from jsni!\n" + s))
    val b2 = new Button("Click me", (_: ClickEvent) => Window.alert("Point: x=" + p2.x + ", y=" + p2.y))
    val b3 = new Button("Click me", (_: ClickEvent) => Window.alert("Point: x=" + p3.x + ", y=" + p3.y))
    val b4 = new Button("Click me", (_: ClickEvent) => Window.alert("Point: x=" + p4.x + ", y=" + p4.y))
    val b5 = new Button("Click me", (_: ClickEvent) => Window.alert("Point: x=" + p5.x + ", y=" + p5.y))
    val b6 = new Button("Click me", (_: ClickEvent) => Window.alert("Point: x=" + p6.x + ", y=" + p6.y))
    RootPanel.get().add(b)
    RootPanel.get().add(b2)
    RootPanel.get().add(b3)
    RootPanel.get().add(b4)
    RootPanel.get().add(b5)
    RootPanel.get().add(b6)
  }
  
  implicit def clickHandler(f: ClickEvent => Unit): ClickHandler = new ClickHandler {
    def onClick(event: ClickEvent) = f(event)
  }
  
  @native def getMessage(x: Int, y: Int): Point = nativeCode {"""
    return [x, y];
  """}
  
  @native def getList(len: Int): JsArrayInteger = nativeCode {"""
    var arr = [];
    for (var i = 0; i < len; i++) {
      arr.push(i);
    }
    return arr;
  """}
}

object Point {
  def apply(x: Int, y: Int): Point = create(x, y)
  
  def unapply(p: Point): Option[(Int, Int)] = Some((p.x, p.y))
  
  @native def create(x: Int, y: Int): Point = nativeCode {"""
    return [x, y];
  """}
}

@SingleJsoImpl(classOf[JsoPoint])
trait Point {
  def x: Int
  def y: Int
}

class JsoPoint protected() extends JavaScriptObject with Point {
  @native def x: Int = nativeCode("return this[0];")
  @native def y: Int = nativeCode("return this[1];")
}

case class ScalaPoint(x: Int, y: Int) extends Point
