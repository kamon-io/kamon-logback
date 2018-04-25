/*
 * =========================================================================================
 * Copyright © 2013-2018 the kamon project <http://kamon.io/>
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

package kamon.logback.instrumentation.aspectj

import kamon.Kamon
import kamon.context.Context
import kamon.logback.instrumentation.{ContextAwareLoggingEvent, Logback}
import kamon.trace.{IdentityProvider, Span}
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.{Around, Aspect, Before, DeclareMixin}
import org.slf4j.MDC

import scala.beans.BeanProperty

@Aspect
class AsyncAppenderInstrumentation {

  @DeclareMixin("ch.qos.logback.classic.spi.ILoggingEvent+")
  def mixinContextAwareLoggingToLoggingEvent: ContextAwareLoggingEvent =
    ContextAwareLoggingEvent()

  @Before("execution(* ch.qos.logback.core.AsyncAppenderBase.append(..)) && args(event)")
  def onAppend(event:ContextAwareLoggingEvent): Unit =
    event.setContext(Kamon.currentContext())

  @Around("execution(* ch.qos.logback.core.spi.AppenderAttachableImpl.appendLoopOnAppenders(..)) && args(event)")
  def onAppendLoopOnAppenders(pjp: ProceedingJoinPoint, event:ContextAwareLoggingEvent): Any =
    Kamon.withContext(event.getContext)(pjp.proceed())

  @Around("execution(* ch.qos.logback.classic.util.LogbackMDCAdapter.getPropertyMap())")
  def aroundGetMDCPropertyMap(pjp: ProceedingJoinPoint): Any = {

    val context = Kamon.currentContext().get(Span.ContextKey)

    if (context.context().traceID != IdentityProvider.NoIdentifier && Logback.mdcContextPropagation){
      MDC.put(Logback.mdcTraceKey, context.context().traceID.string)
      MDC.put(Logback.mdcSpanKey, context.context().spanID.string)
      try {
        pjp.proceed()
      } finally {
        MDC.remove(Logback.mdcTraceKey)
        MDC.remove(Logback.mdcSpanKey)
      }
    } else {
      pjp.proceed()
    }
  }
}

object ContextAwareLoggingEvent  {
  def apply() = new ContextAwareLoggingEvent {
    @volatile @BeanProperty var context: Context = Kamon.currentContext()
  }
}