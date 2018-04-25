/*
 * =========================================================================================
 * Copyright © 2013-2017 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

package kamon.logback.instrumentation

import java.util.concurrent.Callable

import kamon.Kamon
import kanela.agent.api.instrumentation.mixin.Initializer
import kanela.agent.libs.net.bytebuddy.asm.Advice.{Argument, OnMethodExit}
import kanela.agent.libs.net.bytebuddy.implementation.bind.annotation
import kanela.agent.libs.net.bytebuddy.implementation.bind.annotation.{RuntimeType, SuperCall}
import kamon.context.Context
import kanela.agent.scala.KanelaInstrumentation

import scala.beans.BeanProperty

class AsyncAppenderInstrumentation extends KanelaInstrumentation {

  /**
    * Mix:
    *
    * ch.qos.logback.classic.spi.ILoggingEvent with kamon.logback.mixin.ContextAwareLoggingEvent
    */
  forSubtypeOf("ch.qos.logback.classic.spi.ILoggingEvent") { builder =>
    builder
      .withMixin(classOf[ContextAwareLoggingEventMixin])
      .build()
  }


  /**
    * Instrument:
    *
    * ch.qos.logback.core.AsyncAppenderBase::append
    */
  forTargetType("ch.qos.logback.core.AsyncAppenderBase") { builder =>
    builder
      .withAdvisorFor(method("append"), classOf[AppendMethodAdvisor])
      .build()
  }

  /**
    * Instrument:
    *
    * ch.qos.logback.core.spi.AppenderAttachableImpl::appendLoopOnAppenders
    */
  forTargetType("ch.qos.logback.core.spi.AppenderAttachableImpl") { builder =>
    builder
      .withInterceptorFor(method("appendLoopOnAppenders"), classOf[AppendLoopMethodInterceptor])
      .build()
  }
}

/**
  * Mixin for ch.qos.logback.classic.spi.ILoggingEvent
  */
trait ContextAwareLoggingEvent {
  def getContext: Context
  def setContext(context:Context):Unit
}

class ContextAwareLoggingEventMixin extends ContextAwareLoggingEvent {
  @volatile @BeanProperty var context:Context = _

  @Initializer
  def initialize():Unit =
    context = Kamon.currentContext()
}

/**
  * Advisor for ch.qos.logback.core.AsyncAppenderBase::append
  */
class AppendMethodAdvisor
object AppendMethodAdvisor {

  @OnMethodExit
  def onExit(@Argument(0) event:AnyRef): Unit =
    event.asInstanceOf[ContextAwareLoggingEvent].setContext(Kamon.currentContext())
}

/**
  * Interceptor for ch.qos.logback.core.spi.AppenderAttachableImpl::appendLoopOnAppenders
  */
class AppendLoopMethodInterceptor
object AppendLoopMethodInterceptor {

  @RuntimeType
  def aroundAppendLoop(@SuperCall callable: Callable[Int], @annotation.Argument(0) event:AnyRef): Int =
    Kamon.withContext(event.asInstanceOf[ContextAwareLoggingEvent].getContext)(callable.call())
}

