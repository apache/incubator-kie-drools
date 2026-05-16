/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.opentelemetry;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import org.drools.opentelemetry.metrics.MetricsAgendaEventListener;
import org.drools.opentelemetry.tracing.TracingAgendaEventListener;
import org.drools.opentelemetry.tracing.TracingRuleRuntimeEventListener;
import org.kie.api.event.rule.RuleRuntimeEventManager;

/**
 * Entry point for instrumenting a Drools session with OpenTelemetry tracing and metrics.
 *
 * <p>Usage with global OpenTelemetry:
 * <pre>{@code
 * KieSession session = kieBase.newKieSession();
 * DroolsOpenTelemetry.instrument(session);
 * }</pre>
 *
 * <p>Usage with an explicit OpenTelemetry instance:
 * <pre>{@code
 * OpenTelemetry otel = …;
 * KieSession session = kieBase.newKieSession();
 * DroolsOpenTelemetry.instrument(session, otel);
 * }</pre>
 */

public final class DroolsOpenTelemetry {

    static final String INSTRUMENTATION_NAME = "org.drools.opentelemetry";
    static final String INSTRUMENTATION_VERSION = "999-SNAPSHOT";

    private DroolsOpenTelemetry() {
    }

    /**
     * Instruments a KIE session with OpenTelemetry tracing and metrics
     * using the {@link GlobalOpenTelemetry} instance.
     *
     * @param session the session to instrument
     */

    public static void instrument(RuleRuntimeEventManager session) {
        instrument(session, GlobalOpenTelemetry.get());
    }

    /**
     * Instruments a KIE session with OpenTelemetry tracing and metrics.
     *
     * @param session the session to instrument
     * @param openTelemetry the OpenTelemetry instance to use
     */

    public static void instrument(RuleRuntimeEventManager session, OpenTelemetry openTelemetry) {
        instrument(session, openTelemetry, true, true);
    }

    /**
     * Instruments a KIE session with OpenTelemetry, with control over which
     * signals are enabled.
     *
     * @param session the session to instrument
     * @param openTelemetry the OpenTelemetry instance to use
     * @param enableTracing whether to register tracing listeners
     * @param enableMetrics whether to register the metrics listener
     */

    public static void instrument(RuleRuntimeEventManager session, OpenTelemetry openTelemetry,
                                  boolean enableTracing, boolean enableMetrics) {
        if (enableTracing) {
            Tracer tracer = openTelemetry.getTracer(INSTRUMENTATION_NAME, INSTRUMENTATION_VERSION);
            session.addEventListener(new TracingAgendaEventListener(tracer));
            session.addEventListener(new TracingRuleRuntimeEventListener());
        }
        if (enableMetrics) {
            Meter meter = openTelemetry.getMeter(INSTRUMENTATION_NAME);
            session.addEventListener(new MetricsAgendaEventListener(meter));
        }
    }
}