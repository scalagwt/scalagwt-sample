package com.google.gwt.sample.showcase.client

// TODO: Remove once scala.collections.JavaConversions._ can be used
object JavaConversions {
  implicit def javaToScala[T](jit: java.lang.Iterable[T]) = new Siterator(jit.iterator)

  class Siterator[T](private val jit: java.util.Iterator[T]) extends Iterator[T] {
    def hasNext = jit hasNext
    def next = jit next
  }
}
