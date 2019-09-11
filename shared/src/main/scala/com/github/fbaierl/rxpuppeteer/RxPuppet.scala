package com.github.fbaierl.rxpuppeteer

import rx.{Ctx, Obs, Rx, Var}

import scala.language.{existentials, implicitConversions}

object RxPuppet {
  // the puppet master can control any Rx
  implicit def rxToPuppet(rx: Rx[_]): RxPuppet = new RxPuppet(rx)
}

class RxPuppet(val rx: Rx[_]) {

  private var condition: () => Boolean = () => true
  private var runAfter: () => Unit  = () => {}
  private var runBefore: () => Unit = () => {}

  /**
    * Adds a condition to check before triggering the recalc dependency.
    * {{{doBefore(func: => Unit)}}} and {{{doAfterwards(func: => Unit)}}} and  are not affected by this condition.
    * @param condition the condition to check before triggering
    * @return
    */
  def activateIf(condition: => Boolean): RxPuppet = {
    this.condition = () => condition
    this
  }

  /**
    * Code to run after activating the rx dependency.
    * @param thunk code to run
    * @return
    */
  def runAfter(thunk: => Unit): RxPuppet = {
    runAfter = () => thunk
    this
  }

  /**
    * Code to run before activating the rx dependency.
    * @param thunk code to run
    * @return
    */
  def runBefore(thunk: => Unit): RxPuppet = {
    runBefore = () => thunk
    this
  }

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
  def ~~> (otherRxs: Rx[_]*)(implicit ownerCtx: Ctx.Owner): Seq[Obs] = {
    otherRxs.map(otherRx => RxPuppeteer add (rx, otherRx, condition, runBefore, runAfter, false))
  }

  /**
   * Usage:
   * {{{
   * // given x: Rx[T], y: Rx[T]
   * x ~~~> y
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
  def ~~> (otherRx: Rx[_])(implicit ownerCtx: Ctx.Owner): Obs = {
    RxPuppeteer add (rx, otherRx, condition, runBefore, runAfter, false)
  }

  /**
    * Usage:
    * {{{
    * // given x: Rx[T], y: Rx[T], z: Rx[T]
    * x ~~~> (y, z)
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
  def ~~~> (otherRxs: Rx[_]*)(implicit ownerCtx: Ctx.Owner): Seq[Obs] = {
    otherRxs.map(otherRx => RxPuppeteer add (rx, otherRx, condition, runBefore, runAfter, true))
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
  def ~~~> (otherRx: Rx[_])(implicit ownerCtx: Ctx.Owner): Obs = {
    RxPuppeteer add (rx, otherRx, condition, runBefore, runAfter, true)
  }
}
