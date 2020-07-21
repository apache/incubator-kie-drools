/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.service.messaging;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.Message;
import org.kie.kogito.trusty.storage.api.model.MessageExceptionField;
import org.kie.kogito.trusty.storage.api.model.TypedValue;
import org.testcontainers.shaded.org.apache.commons.lang.builder.CompareToBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

public class TraceEventTestUtils {

    public static void assertDecision(Decision expected, Decision actual) {
        assertEquals(expected.getExecutionId(), actual.getExecutionId());
        assertSame(expected.getExecutionType(), actual.getExecutionType());
        assertEquals(expected.getExecutionTimestamp(), actual.getExecutionTimestamp());
        assertEquals(expected.getExecutedModelName(), actual.getExecutedModelName());
        assertEquals(expected.getExecutorName(), actual.getExecutorName());
        assertList(expected.getInputs(), actual.getInputs(), TraceEventTestUtils::assertTypedValue, TraceEventTestUtils::compareTypedValue);
        assertList(expected.getOutcomes(), actual.getOutcomes(), TraceEventTestUtils::assertDecisionOutcome, TraceEventTestUtils::compareDecisionOutcome);
    }

    public static void assertDecisionOutcome(DecisionOutcome expected, DecisionOutcome actual) {
        assertEquals(expected.getOutcomeId(), actual.getOutcomeId());
        assertEquals(expected.getOutcomeName(), actual.getOutcomeName());
        assertTypedValue(expected.getOutcomeResult(), actual.getOutcomeResult());
        assertEquals(expected.getEvaluationStatus(), actual.getEvaluationStatus());
        assertList(expected.getOutcomeInputs(), actual.getOutcomeInputs(), TraceEventTestUtils::assertTypedValue, TraceEventTestUtils::compareTypedValue);
        assertList(expected.getMessages(), actual.getMessages(), TraceEventTestUtils::assertMessage, TraceEventTestUtils::compareMessage);
    }

    public static <T> void assertList(Collection<T> expected, Collection<T> actual, BiConsumer<T, T> itemAssertor, Comparator<? super T> comparator) {
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

    public static void assertTypedValue(TypedValue expected, TypedValue actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getTypeRef(), actual.getTypeRef());
        assertEquals(expected.getValue(), actual.getValue());
    }

    public static int compareDecisionOutcome(DecisionOutcome expected, DecisionOutcome actual) {
        return new CompareToBuilder()
                .append(expected.getOutcomeId(), actual.getOutcomeId())
                .append(expected.getOutcomeName(), actual.getOutcomeName())
                .append(expected.getEvaluationStatus(), actual.getEvaluationStatus())
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

    public static int compareTypedValue(TypedValue expected, TypedValue actual) {
        return new CompareToBuilder()
                .append(expected.getTypeRef(), actual.getTypeRef())
                .append(expected.getName(), actual.getName())
                .toComparison();
    }
}
