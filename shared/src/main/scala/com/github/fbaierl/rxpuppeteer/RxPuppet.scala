package com.github.fbaierl.rxpuppeteer

import rx.{Obs, Rx, Var}

import scala.language.{existentials, implicitConversions}

object RxPuppet {
  // the puppet master can control any Rx
  implicit def rxToPuppet(rx: Rx[_]): RxPuppet = new RxPuppet(rx)
}

class RxPuppet(val rx: Rx[_]) {
  /**
    * Usage:
    * {{{
    * // given x: Rx[T], y: Rx[T], z: Rx[T]
    * x ~~> (y, z)
    * }}}
    *
    * This checks for cyclic dependencies and duplicates and is otherwise functionally equivalent to writing:
    * {{{
    * // given x: Rx[T], y: Rx[T], z: Rx[T]
    * x.triggerLater {
    *    y.recalc()
    * }
    *
    * x.triggerLater {
    *    z.recalc()
    * }
    * }}}
    *
    */
  def ~~> (otherRxs: Rx[_]*): Seq[Obs] = {
    otherRxs.map(otherRx => RxPuppeteer add (rx, otherRx))
  }

/**
  * Usage:
  * {{{
  * // given x: Rx[T], y: Rx[T]
  * x ~~> y
  * }}}
  *
  * This checks for cyclic dependencies and duplicates and is otherwise functionally equivalent to writing:
  * {{{
  * // given x: Rx[T], y: Rx[T]
  * private val onXUpdated = x.triggerLater {
  *    y.recalc()
  * }
  * }}}
  */
  def ~~> (otherRx: Rx[_]): Obs = {
    RxPuppeteer add (rx, otherRx)
  }

  // TODO method that allows duplicates

  // TODO method that calls code before

  // TODO method that calls code afterwards

}
