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

import com.google.gwt.event.logical.shared._
import com.google.gwt.event.dom.client._

/**
 * Provides implicit conversions that allow functions to be substituted where handlers are called for.
 */
object Handlers {
  // ChangeHandler
  implicit def fn2changeHandler(fn: ChangeEvent => Unit): ChangeHandler =
    new ChangeHandler() {
      def onChange(event: ChangeEvent) = fn(event)
    }
  implicit def body2changeHandler(body: => Unit): ChangeHandler =
    new ChangeHandler() {
      def onChange(event: ChangeEvent) = body
    }

  // ClickHandler
  implicit def fn2clickHandler(fn: ClickEvent => Unit): ClickHandler =
    new ClickHandler() {
      def onClick(event: ClickEvent) = fn(event)
    }
  implicit def body2clickHandler(body: => Unit): ClickHandler =
    new ClickHandler() {
      def onClick(event: ClickEvent) = body
    }

  // KeyUpHandler
  implicit def fn2keyUpHandler(fn: KeyUpEvent => Unit): KeyUpHandler =
    new KeyUpHandler {
      def onKeyUp(event: KeyUpEvent) = fn(event)
    }
  implicit def body2keyUpHandler(body: => Unit): KeyUpHandler =
    new KeyUpHandler {
      def onKeyUp(event: KeyUpEvent) = body
    }

  // SelectionHandler
  implicit def fn2selectionHandler[T](fn: SelectionEvent[T] => Unit): SelectionHandler[T] =
    new SelectionHandler[T] {
      def onSelection(event: SelectionEvent[T]): Unit = fn(event)
    }

  // ValueChangeHandler
  implicit def fn2valueChangeHandler[T](fn: ValueChangeEvent[T] => Unit): ValueChangeHandler[T] =
    new ValueChangeHandler[T] {
      def onValueChange(event: ValueChangeEvent[T]): Unit = fn(event)
    }

  // OpenHandler
  implicit def fn2openHandler[T](fn: OpenEvent[T] => Unit): OpenHandler[T] =
    new OpenHandler[T] {
      def onOpen(event: OpenEvent[T]): Unit = fn(event)
    }
}


