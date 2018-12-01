# scala-rx-puppeteer

Scala-rx-puppeteer is an utility library for [Scala.Rx](https://github.com/lihaoyi/scala.rx) -  an experimental change propagation library for [Scala](http://www.scala-lang.org/).

In my case, working with [Scala.Rx](https://github.com/lihaoyi/scala.rx) let to a lot of code looking somewhat like this:
```scala

val x = Var(1)
val y = Var(2)

val onXChange = x.triggerLater {
  // do something with the data of x
}

val onYChange = y.triggerLater {
  // do something with the data of y 
  // also: in order to correctly work with y, we need data x
}

// if the value of x changes; also notify the observers of y
x.triggerLater {
   y.recalc()
}

```
My use cases were mostly that I needed to trigger a redraw in my [Scala.js](https://www.scala-js.org/) application everytime `x` changes. Also data `y`'s representation depends on `x`, so everytime `x` changes, the observers of `y` need to be triggered as well so that all data depending on `y` is redrawn as well.

**The Problem:** Handling these dependencies can easily get out-of-hand because of cyclic relationships and duplicates. Finding these errors quickly was becoming a problem as the application grew.
 
 using scala-rx-puppeteer, the above can be rewritten like this:
 ```scala
 import // TODO 
 
 val x = Var(1)
 val y = Var(2)
 
 val onXChange = x.triggerLater {
   // ..
 }
 
 val onYChange = y.triggerLater {
   // ..
 }
 
 // this is where the magic happens
 x ~~~> y 
 ```

Writing `x ~~~> y` in this case equals `x.triggerLater { y.recalc() }` but throws an AssertionError if this dependency introduces a cyclic relationship or has already been added once.

# Getting Started

 
Scala-rx-puppeteer is available on [Maven Central](http://search.maven.org/#artifactdetails%7Ccom.scalarx%7Cscalarx_2.10%7C0.1%7Cjar). 
In order to get started, simply add the following to your `build.sbt`:

```scala
libraryDependencies += todo
```

For **Scala.js** add:

```scala
libraryDependencies += todo
```

