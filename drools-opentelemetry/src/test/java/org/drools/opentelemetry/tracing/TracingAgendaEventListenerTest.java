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
package org.drools.opentelemetry.tracing;

import java.util.Collections;
import java.util.List;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import io.opentelemetry.sdk.trace.data.SpanData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TracingAgendaEventListenerTest {

    @RegisterExtension
    static final OpenTelemetryExtension otelTesting = OpenTelemetryExtension.create();

    @Test
    void shouldCreateSpanOnRuleFiring() {
        Tracer tracer = otelTesting.getOpenTelemetry()
                .getTracer(TracingAgendaEventListener.INSTRUMENTATION_NAME);
        TracingAgendaEventListener listener = new TracingAgendaEventListener(tracer);
        Rule rule = mockRule("testRule", "com.example");
        Match match = mockMatch(rule, 2);
        BeforeMatchFiredEvent beforeEvent = mock(BeforeMatchFiredEvent.class);
        when(beforeEvent.getMatch()).thenReturn(match);
        AfterMatchFiredEvent afterEvent = mock(AfterMatchFiredEvent.class);
        when(afterEvent.getMatch()).thenReturn(match);
        listener.beforeMatchFired(beforeEvent);
        listener.afterMatchFired(afterEvent);
        List<SpanData> spans = otelTesting.getSpans();
        assertThat(spans).hasSize(1);
        SpanData span = spans.get(0);
        assertThat(span.getName()).isEqualTo("rule: testRule");
        assertThat(span.getAttributes().get(TracingAgendaEventListener.ATTR_RULE_NAME))
                .isEqualTo("testRule");
        assertThat(span.getAttributes().get(TracingAgendaEventListener.ATTR_RULE_PACKAGE))
                .isEqualTo("com.example");
        assertThat(span.getAttributes().get(TracingAgendaEventListener.ATTR_FACT_COUNT))
                .isEqualTo(2L);
    }

    private Rule mockRule(String name, String packageName) {
        Rule rule = mock(Rule.class);
        when(rule.getName()).thenReturn(name);
        when(rule.getPackageName()).thenReturn(packageName);
        return rule;
    }

    @SuppressWarnings("unchecked")
    private Match mockMatch(Rule rule, int factCount) {
        Match match = mock(Match.class);
        when(match.getRule()).thenReturn(rule);
        List<FactHandle> handles = Collections.nCopies(factCount, mock(FactHandle.class));
        when(match.getFactHandles()).thenReturn((List) handles);
        return match;
    }
}