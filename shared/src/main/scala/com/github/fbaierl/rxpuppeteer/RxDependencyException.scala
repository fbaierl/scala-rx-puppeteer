package com.github.fbaierl.rxpuppeteer

final case class RxDependencyException(private val message: String = "",
                                 private val cause: Throwable = None.orNull) extends Exception(message, cause)