package com.google.gwt.sample.gwtdlx.client

import com.google.gwt.core.client.Scheduler
import util.continuations._
import com.google.gwt.core.client.Scheduler.{RepeatingCommand, ScheduledCommand}

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

/**
 * Control structures for breaking up a long-running computation, built on GWT Scheduler infrastructure.
 *
 * @author Aaron Novstrup
 */
object Scheduled {
   sealed trait Schedule { def schedule: Unit }
   type schedulable = cps[Schedule]
   type ScheduleCont = Unit => Schedule

   /**
    * Delimit a continuation boundary for scheduling primitives (defer, yieldc, sleep).
    */
   def sched(f: => Unit @schedulable): Unit = {
      val s = reset[Schedule, Schedule] {
         f
         Done
      }
      s.schedule
   }

   /**
    * Defer the rest of the computation (up to the continuation boundary) to continue after the browser's event loop is
    * processed.
    *
    * @see Scheduler#scheduleDeferred
    */
   def defer: Unit @cps[Schedule] = shift {
      (k: ScheduleCont) => Deferred(k)
   }

   /**
    * Yield control, allowing the scheduler to give time to other tasks. The rest of the computation (up to the
    * continuation boundary) may be deferred until after the browser's event loop is processed.
    *
    * @see Scheduler#scheduleIncremental
    */
   def yieldc: Unit @cps[Schedule] = shift {
      (k: ScheduleCont) => Incremental(k)
   }

   /**
    * Defer the rest of the compuation (up to the continuation boundary) to occur after a delay. Note that the actual
    * delay may be longer than the specified delay.
    *
    * @see Schedule#scheduleFixedDelay
    */
   def sleep(delayMs: Int) : Unit @schedulable = shift {
      (k: ScheduleCont) =>  Delayed(k, delayMs)
   }

   private case class Deferred(k: ScheduleCont) extends Schedule with ScheduledCommand {
      def schedule = Scheduler.get.scheduleDeferred(this)

      def execute = k(()).schedule
   }

   private case class Incremental(k: ScheduleCont) extends Schedule with RepeatingCommand {
      private var remaining: ScheduleCont = k

      def schedule = Scheduler.get.scheduleIncremental(this)

      def execute = {
         remaining(()) match {
            case Incremental(k2) => {
               remaining = k2
               true
            }
            case s => {
               s.schedule
               false
            }
         }
      }
   }

   private case class Delayed(k: ScheduleCont, delayMs: Int) extends Schedule with RepeatingCommand {
      def schedule = Scheduler.get.scheduleFixedDelay(this, delayMs)

      def execute = {
         k(()).schedule
         false
      }
   }

   private case object Done extends Schedule {
      def schedule = {}
   }
}