package com.github.fbaierl.rxpuppeteer

import com.github.fbaierl.tarjan.TarjanRecursive._
import rx.Ctx.Owner.Unsafe._
import rx.{Obs, Rx}

import scala.collection.mutable
import scala.language.{existentials, implicitConversions}

/**
  * Manages rx "dependencies".
  */
object RxPuppeteer {

  private var deps: List[(Rx[_], Rx[_])] = List()

  private def activate (x: Rx[_], y: Rx[_]): Obs = {
    x.triggerLater{
      y.recalc()
    }
  }

  private def cyclic: List[(Rx[_], Rx[_])] => Boolean = ds => {
    var eles = mutable.MutableList.empty[Rx[_]]
    ds.foreach { d =>
      if(!eles.contains(d._1)){
        eles += d._1
      }
      if(!eles.contains(d._2)){
        eles += d._2
      }
    }
    // input format needed for tarjan
    val tarjanInput = mutable.Map.empty[Int, List[Int]]
    eles.foreach { el =>
      tarjanInput.put(eles.indexOf(el), ds.filter(p => p._1 == el).map(p => eles.indexOf(p._2)))
    }
    tarjan(collection.immutable.Map(tarjanInput.toList: _*)).exists(partition => partition.size > 1)
  }

  /**
    * Activates a rx dependency x => y, which is the same as writing:
    * {{{
    * x.triggerLater { y.recalc() }
    * }}}, but only if this does not add a cyclic or duplicate relationship to the previously activated rx dependencies.
    */
  def add (x: Rx[_], y: Rx[_]): Obs = {
    val newList: List[(Rx[_], Rx[_])] = (x,y) :: deps
    if(cyclic(newList) || x == y){
      throw RxDependencyException(
        s"""Unable to add rx dependency:
            | $x --> $y
            | Reason: Dependency would introduce a cyclic relationship!""".stripMargin)
    }
    if(deps.contains((x,y))){
      throw RxDependencyException(
        s"""Did not add rx dependency:
           | $x --> $y
           | Reason: Dependency already activated!""".stripMargin)
    } else {
      deps = newList
      activate(x, y)
    }
  }
}
