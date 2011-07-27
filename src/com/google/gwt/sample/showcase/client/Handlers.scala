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
object Handlers extends ChangeHandlers
  with ClickHandlers
  with KeyUpHandlers
  with KeyDownHandlers
  with SelectionHandlers
  with ValueChangeHandlers
  with OpenHandlers

trait ChangeHandlers {
  implicit def fn2changeHandler(fn: ChangeEvent => Unit): ChangeHandler =
    new ChangeHandler() {
      def onChange(event: ChangeEvent) = fn(event)
    }
  implicit def enrichHasChangeHandlers(o: HasChangeHandlers): RichHasChangeHandlers =
    new RichHasChangeHandlers(o)

  class RichHasChangeHandlers(o: HasChangeHandlers) {
    def onChange(fn: ChangeEvent => Unit) = o.addChangeHandler(fn)
  }
}
object ChangeHandlers extends ChangeHandlers

trait ClickHandlers {
  implicit def fn2clickHandler(fn: ClickEvent => Unit): ClickHandler =
    new ClickHandler() {
      def onClick(event: ClickEvent) = fn(event)
    }
  implicit def enrichHasClickHandlers(o: HasClickHandlers): RichHasClickHandlers =
    new RichHasClickHandlers(o)

  class RichHasClickHandlers(o: HasClickHandlers) {
    def onClick(fn: ClickEvent => Unit) = o.addClickHandler(fn)
  }
}
object ClickHandlers extends ClickHandlers

trait KeyUpHandlers {
  implicit def fn2keyUpHandler(fn: KeyUpEvent => Unit): KeyUpHandler =
    new KeyUpHandler {
      def onKeyUp(event: KeyUpEvent) = fn(event)
    }
  implicit def enrichHasKeyUpHandlers(o: HasKeyUpHandlers): RichHasKeyUpHandlers =
    new RichHasKeyUpHandlers(o)

  class RichHasKeyUpHandlers(o: HasKeyUpHandlers) {
    def onKeyUp(fn: KeyUpEvent => Unit) = o.addKeyUpHandler(fn)
  }
}
object KeyUpHandlers extends KeyUpHandlers

trait KeyDownHandlers {
  implicit def fn2keyDownHandler(fn: KeyDownEvent => Unit): KeyDownHandler =
    new KeyDownHandler {
      def onKeyDown(event: KeyDownEvent) = fn(event)
    }
  implicit def enrichHasKeyDownHandlers(o: HasKeyDownHandlers): RichHasKeyDownHandlers =
    new RichHasKeyDownHandlers(o)

  class RichHasKeyDownHandlers(o: HasKeyDownHandlers) {
    def onKeyDown(fn: KeyDownEvent => Unit) = o.addKeyDownHandler(fn)
  }
}
object KeyDownHandlers extends KeyDownHandlers

trait SelectionHandlers {
  implicit def fn2selectionHandler[T](fn: SelectionEvent[T] => Unit): SelectionHandler[T] =
    new SelectionHandler[T] {
      def onSelection(event: SelectionEvent[T]): Unit = fn(event)
    }
  implicit def enrichHasSelectionHandlers[T](o: HasSelectionHandlers[T]): RichHasSelectionHandlers[T] =
    new RichHasSelectionHandlers[T](o)

  class RichHasSelectionHandlers[T](o: HasSelectionHandlers[T]) {
    def onSelection(fn: SelectionEvent[T] => Unit) = o.addSelectionHandler(fn)
  }
}
object SelectionHandlers extends SelectionHandlers

trait ValueChangeHandlers {
  implicit def fn2valueChangeHandler[T](fn: ValueChangeEvent[T] => Unit): ValueChangeHandler[T] =
    new ValueChangeHandler[T] {
      def onValueChange(event: ValueChangeEvent[T]): Unit = fn(event)
    }
  implicit def enrichHasValueChangeHandlers[T](o: HasValueChangeHandlers[T]): RichHasValueChangeHandlers[T] =
    new RichHasValueChangeHandlers[T](o)

  class RichHasValueChangeHandlers[T](o: HasValueChangeHandlers[T]) {
    def onValueChange(fn: ValueChangeEvent[T] => Unit) = o.addValueChangeHandler(fn)
  }
}
object ValueChangeHandlers extends ValueChangeHandlers

trait OpenHandlers {
  implicit def fn2openHandler[T](fn: OpenEvent[T] => Unit): OpenHandler[T] =
    new OpenHandler[T] {
      def onOpen(event: OpenEvent[T]): Unit = fn(event)
    }
  implicit def enrichHasOpenHandlers[T](o: HasOpenHandlers[T]): RichHasOpenHandlers[T] =
    new RichHasOpenHandlers[T](o)

  class RichHasOpenHandlers[T](o: HasOpenHandlers[T]) {
    def onOpen(fn: OpenEvent[T] => Unit) = o.addOpenHandler(fn)
  }
}
object OpenHandlers extends OpenHandlers


