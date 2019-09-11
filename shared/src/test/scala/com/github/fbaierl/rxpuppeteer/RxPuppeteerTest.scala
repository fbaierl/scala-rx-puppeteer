package com.github.fbaierl.rxpuppeteer

import org.scalatest.FlatSpec
import rx._
import com.github.fbaierl.rxpuppeteer.RxPuppet.rxToPuppet

class RxPuppeteerTest extends FlatSpec {

  implicit val owner: Ctx.Owner = Ctx.Owner.safe()

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

  it should "should work with multiple targets." in {
    val x = Var(0)
    val y = Var(0)
    val z = Var(0)
    var zTriggered = false
    var yTriggered = false
    z.triggerLater { zTriggered = true }
    y.triggerLater { yTriggered = true}

    x() = 1
    assert(!zTriggered)
    assert(!yTriggered)
    x ~~> (y, z)
    x() = 2
    assert(zTriggered)
    assert(yTriggered)
  }

  it should "work with conditions." in {
    val x = Var(0)
    val y = Var(0)
    var triggered = 0
    def condition: Boolean = x.now == 2

    y.triggerLater {
      triggered += 1
    }

    assert(triggered == 0)
    (x activateIf condition) ~~> y
    x() = 1 // should not trigger since condition is still false
    assert(triggered == 0)
    x() = 2
    assert(triggered == 1) // triggered b/c condition is true

  }

  it should "work with runBefore and runAfter." in {
    val x = Var(0)
    val y = Var(0)
    var triggeredBefore = 0
    var triggeredAfter = 0
    def condition = false

    (x runBefore  { triggeredBefore += 1 }
       runAfter   { triggeredAfter += 1 }
       activateIf condition
      ) ~~~> y

    assert(triggeredBefore == 0)
    assert(triggeredAfter == 0)

    x() = 1

    assert(triggeredBefore == 1)
    assert(triggeredAfter == 1)

  }
}
