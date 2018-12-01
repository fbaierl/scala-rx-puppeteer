package com.github.fbaierl.rxpuppeteer

import rx.{Rx, Var}

import scala.language.{existentials, implicitConversions}

object RxPuppet {
  // the puppet master can control any Var
  implicit def rxToPuppet(rx: Rx[_]): RxPuppet = new RxPuppet(rx)
}

class RxPuppet(val rx: Rx[_]) {
  /**
    * Usage:
    * {{{
    *    // given x: Rx[T], y: Rx[T]
    *    x ~~~> y
    * }}}
    *
    * This checks for cyclic dependencies and duplicates and is otherwise functionally equivalent to writing:
    * {{{
    *   // given x: Rx[T], y: Rx[T]
    *   private val onXUpdated = x.triggerLater {
    *      y.recalc()
    *   }
    * }}}
    *
    */
  def ~~> (otherRx: Var[_]): Unit = {
    RxPuppeteer ++ (rx, otherRx)
  }

  // TODO method that allows duplicates

  // TODO method that calls code before

  // TODO method that calls code afterwards

}
