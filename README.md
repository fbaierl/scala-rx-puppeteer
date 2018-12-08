# scala-rx-puppeteer ~~> ~~~>

Scala-rx-puppeteer is an utility library for [Scala.Rx](https://github.com/lihaoyi/scala.rx) 
(an experimental change propagation library for [Scala](http://www.scala-lang.org/)) that aims to nullify 
the risk of introducing cyclic dependencies.

These cyclic dependencies might be introduced when triggering the recalculation of an [Rx](http://www.lihaoyi.com/scala.rx/#rx.core.Rx) while performing side effects 
on an observer ([Obs](http://www.lihaoyi.com/scala.rx/#rx.core.Obs)) change.

Scala-rx-puppeteer offers some alternative syntax to create those dependencies and **throws an exception** if a dependency
is added that would lead to a cyclic dependency.

# Background

In my Scala applications I got used to using [Scala.Rx](https://github.com/lihaoyi/scala.rx) extensively. 
For better or worse, this lead to a lot of code looking somewhat like this:
```scala
val x = Var(1)
val y = Var(2)

val onXChange = x.triggerLater {
  // do something with the data of x
}

val onYChange = y.triggerLater {
  // do something with the data of y 
  // also: in order to correctly work with y, we indirectly need data x
}

// if the value of x changes; also notify the observers of y
x.triggerLater {
   y.recalc()
}
```
Specifically, I noticed that I had use `recalc()` inside `triggerLater` code blocks quite often.
My use cases were mostly that I needed to trigger a redraw in my [Scala.js](https://www.scala-js.org/) application every time `x` changes. Also data `y`'s representation *indirectly* depends on `x`, so everytime `x` changes, the observers of `y` need to be triggered as well so that all data depending on `y` is redrawn as well.

**The Problem:** Handling these dependencies can easily get out-of-hand because of cyclic relationships and duplicates. Finding these errors quickly was becoming a problem as the application grew larger and more complex.

# HOWTO  
 
 
**Basic example:** Using scala-rx-puppeteer, the example given above can be rewritten like this:
```scala
import com.github.fbaierl.rxpuppeteer.RxPuppet.rxToPuppet

val x = Var(1)
val y = Var(2)

val onXChange = x.triggerLater {
  // ..
}

val onYChange = y.triggerLater {
  // ..
}

x ~~~> y 
```

Writing `x ~~~> y` in this case equals `x.triggerLater { y.recalc() }` but throws an AssertionError if this dependency introduces a cyclic relationship or has already been added once.

**Advanced example:** Code can be executed before and after the change propagation. Also, the change propagation can be bound to a condition.
```scala

val x = Var(1)
val y = Var(2)
val condition = false

x.trigger {
  /* code to run before change propagation */}
  if(condition){
    y.recalc()
  }
  /* code to run after after propagation */
}

```
Can be rewritten using rx-puppeteer like this:
```scala
import com.github.fbaierl.rxpuppeteer.RxPuppet.rxToPuppet

val x = Var(1)
val y = Var(2)
def condition: Boolean = false

(x runBefore  {/* code to run before change propagation */}
   runAfter   {/* code to run after change propagation */}
   activateIf condition
  ) ~~> y 
```

`~~>` results in **trigger** being called on the Rx, 
`~~~>` results in **triggerLater** being called on the Rx.

# Getting Started

 
Scala-rx-puppeteer is available on [Maven Central](http://search.maven.org/#artifactdetails%7Ccom.scalarx%7Cscalarx_2.10%7C0.1%7Cjar). 
In order to get started, simply add the following to your `build.sbt`:

```scala
libraryDependencies += "com.github.fbaierl" %% "scala-rx-puppeteer" % "0.1.2"
```

For **Scala.js** add:

```scala
libraryDependencies += "com.github.fbaierl" %%% "scala-rx-puppeteer" % "0.1.2"
```

# License

Copyright 2018 Florian Baierl

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.