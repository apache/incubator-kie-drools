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
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;

/**
 * A {@link RuleRuntimeEventListener} that records fact lifecycle operations as
 * span events on the current OpenTelemetry span.
 * <p>
 * When facts are inserted, updated, or deleted, an event is added to the active span
 * with the fact's class name, enabling end-to-end tracing of working memory mutations
 * within a rule execution context.
 */

public class TracingRuleRuntimeEventListener implements RuleRuntimeEventListener {

    static final AttributeKey<String> ATTR_FACT_CLASS = AttributeKey.stringKey("drools.fact.class");
    static final AttributeKey<String> ATTR_RULE_NAME = AttributeKey.stringKey("drools.rule.name");

    @Override
    public void objectInserted(ObjectInsertedEvent event) {
        Span current = Span.current();
        if (current.getSpanContext().isValid()) {
            Attributes attrs = buildFactAttributes(event.getObject(),
                    event.getRule() != null ? event.getRule().getName() : null);
            current.addEvent("fact.inserted", attrs);
        }
    }

    @Override
    public void objectUpdated(ObjectUpdatedEvent event) {
        Span current = Span.current();
        if (current.getSpanContext().isValid()) {
            Attributes attrs = buildFactAttributes(event.getObject(),
                    event.getRule() != null ? event.getRule().getName() : null);
            current.addEvent("fact.updated", attrs);
        }
    }

    @Override
    public void objectDeleted(ObjectDeletedEvent event) {
        Span current = Span.current();
        if (current.getSpanContext().isValid()) {
            Attributes attrs = buildFactAttributes(event.getOldObject(),
                    event.getRule() != null ? event.getRule().getName() : null);
            current.addEvent("fact.deleted", attrs);
        }
    }

    private Attributes buildFactAttributes(Object fact, String ruleName) {
        String className = fact != null ? fact.getClass().getSimpleName() : "null";
        if (ruleName != null) {
            return Attributes.of(ATTR_FACT_CLASS, className, ATTR_RULE_NAME, ruleName);
        }
        return Attributes.of(ATTR_FACT_CLASS, className);
    }
}