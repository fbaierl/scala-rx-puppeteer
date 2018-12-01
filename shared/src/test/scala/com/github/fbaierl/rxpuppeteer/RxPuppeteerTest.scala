package com.github.fbaierl.rxpuppeteer

import org.scalatest.FlatSpec
import rx._
import rx.Ctx.Owner.Unsafe._
import com.github.fbaierl.rxpuppeteer.RxPuppet.rxToPuppet

class RxPuppeteerTest extends FlatSpec {

  "~~>" should "should trigger recalc()." in {
    val x = Var(0)
    val y = Var(0)
    val z = Rx(x() + y())
    // a does not have any connection to x,y,z
    var triggered = false
    val a = Var(0)

    a.triggerLater {
      triggered = true

    }

    x() = 1

    assert(z.now == 1)
    assert(!triggered)

    x ~~> a
    x() = 2

    assert(z.now == 2)
    assert(triggered)
  }

  it should "detect cyclic dependencies." in {
    val x = Var(0)
    val y = Var(0)
    val z = Var(0)
    val w = Var(0)

    x ~~> y
    y ~~> z

    assertThrows[RxDependencyException] {
      z ~~> x
    }
  }

  it should "detect cyclic dependencies (self-reference)." in {
    val x = Var(0)
    assertThrows[RxDependencyException] {
      x ~~> x
    }
  }

}
