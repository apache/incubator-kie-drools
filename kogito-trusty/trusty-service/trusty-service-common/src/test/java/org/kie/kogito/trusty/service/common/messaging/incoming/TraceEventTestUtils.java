/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.trusty.service.common.messaging.incoming;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.tracing.event.message.Message;
import org.kie.kogito.tracing.event.message.MessageExceptionField;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionOutcome;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

public class TraceEventTestUtils {

    public static void assertDecision(Decision expected, Decision actual) {
        assertSame(expected.getExecutionType(), actual.getExecutionType());
        assertEquals(expected.getExecutionId(), actual.getExecutionId());
        assertEquals(expected.getSourceUrl(), actual.getSourceUrl());
        assertEquals(expected.getExecutionTimestamp(), actual.getExecutionTimestamp());
        assertEquals(expected.hasSucceeded(), actual.hasSucceeded());
        assertEquals(expected.getExecutedModelName(), actual.getExecutedModelName());
        assertEquals(expected.getExecutorName(), actual.getExecutorName());
        assertList(expected.getInputs(), actual.getInputs(), TraceEventTestUtils::assertDecisionInput, TraceEventTestUtils::compareDecisionInput);
        assertList(expected.getOutcomes(), actual.getOutcomes(), TraceEventTestUtils::assertDecisionOutcome, TraceEventTestUtils::compareDecisionOutcome);
    }

    public static void assertDecisionInput(DecisionInput expected, DecisionInput actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertTypedValue(expected.getValue(), actual.getValue());
    }

    public static void assertDecisionOutcome(DecisionOutcome expected, DecisionOutcome actual) {
        assertEquals(expected.getOutcomeId(), actual.getOutcomeId());
        assertEquals(expected.getOutcomeName(), actual.getOutcomeName());
        assertTypedValue(expected.getOutcomeResult(), actual.getOutcomeResult());
        assertEquals(expected.getEvaluationStatus(), actual.getEvaluationStatus());
        assertList(expected.getOutcomeInputs(),
                actual.getOutcomeInputs(),
                TraceEventTestUtils::assertTypedValue,
                TraceEventTestUtils::compareTypedValue);
        assertList(expected.getMessages(),
                actual.getMessages(),
                TraceEventTestUtils::assertMessage,
                TraceEventTestUtils::compareMessage);
    }

    public static <T> void assertList(Collection<T> expected,
            Collection<T> actual,
            BiConsumer<T, T> itemAssertor,
            Comparator<? super T> comparator) {
        if (expected == null && actual == null
                || expected == null && actual.isEmpty()
                || actual == null && expected.isEmpty()) {
            return;
        }

        assertNotNull(expected);
        assertNotNull(actual);
        assertSame(expected.size(), actual.size());

        List<T> sortedExpected = expected.stream().sorted(comparator).collect(Collectors.toList());
        List<T> sortedActual = actual.stream().sorted(comparator).collect(Collectors.toList());

        for (int i = 0; i < sortedExpected.size(); i++) {
            itemAssertor.accept(sortedExpected.get(0), sortedActual.get(0));
        }
    }

    public static void assertMessage(Message expected, Message actual) {
        assertSame(expected.getLevel(), actual.getLevel());
        assertEquals(expected.getCategory(), actual.getCategory());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getSourceId(), actual.getSourceId());
        assertEquals(expected.getText(), actual.getText());
        assertMessageExceptionField(expected.getException(), actual.getException());
    }

    public static void assertMessageExceptionField(MessageExceptionField expected, MessageExceptionField actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null || actual == null) {
            fail();
        }
        assertEquals(expected.getMessage(), actual.getMessage());
        assertEquals(expected.getClassName(), actual.getClassName());
        assertMessageExceptionField(expected.getCause(), actual.getCause());
    }

    public static void assertTypedValue(NamedTypedValue expected,
            NamedTypedValue actual) {
        assertEquals(expected.getName(), actual.getName());
        assertTypedValue(expected.getValue(), actual.getValue());
    }

    public static void assertTypedValue(TypedValue expected,
            TypedValue actual) {
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.toUnit().getBaseType(), actual.toUnit().getBaseType());
        assertEquals(expected.toUnit().getValue(), actual.toUnit().getValue());
    }

    public static int compareDecisionInput(DecisionInput expected, DecisionInput actual) {
        return new CompareToBuilder()
                .append(expected.getId(), actual.getId())
                .append(expected.getName(), actual.getName())
                .append(expected.getValue(), actual.getValue(), toObjectComparator(TypedValue.class,
                        TraceEventTestUtils::compareTypedValue))
                .toComparison();
    }

    public static int compareDecisionOutcome(DecisionOutcome expected, DecisionOutcome actual) {
        return new CompareToBuilder()
                .append(expected.getOutcomeId(), actual.getOutcomeId())
                .append(expected.getOutcomeName(), actual.getOutcomeName())
                .append(expected.getEvaluationStatus(), actual.getEvaluationStatus())
                .append(expected.getOutcomeResult(), actual.getOutcomeResult(), toObjectComparator(TypedValue.class,
                        TraceEventTestUtils::compareTypedValue))
                .toComparison();
    }

    public static int compareMessage(Message expected, Message actual) {
        return new CompareToBuilder()
                .append(expected.getLevel(), actual.getLevel())
                .append(expected.getCategory(), actual.getCategory())
                .append(expected.getType(), actual.getType())
                .append(expected.getText(), actual.getText())
                .toComparison();
    }

    public static int compareTypedValue(NamedTypedValue expected,
            NamedTypedValue actual) {
        return new CompareToBuilder()
                .append(expected.getName(), actual.getName())
                .append(expected.getValue().getKind(), actual.getValue().getKind())
                .append(expected.getValue().toUnit().getBaseType(), actual.getValue().toUnit().getBaseType())
                .append(expected.getValue().getType(), actual.getValue().getType())
                .toComparison();
    }

    public static int compareTypedValue(TypedValue expected,
            TypedValue actual) {
        return new CompareToBuilder()
                .append(expected.getType(), actual.getType())
                .append(expected.toUnit().getBaseType(), actual.toUnit().getBaseType())
                .append(expected.toUnit().getValue(), actual.toUnit().getValue())
                .toComparison();
    }

    public static <T> Comparator<Object> toObjectComparator(Class<T> clazz, Comparator<T> comparator) {
        return (o1, o2) -> comparator.compare(clazz.cast(o1), clazz.cast(o2));
    }
}
