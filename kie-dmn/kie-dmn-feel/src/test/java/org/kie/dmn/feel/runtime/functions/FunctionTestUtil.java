/**
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
package org.kie.dmn.feel.runtime.functions;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractComparableAssert;
import org.assertj.core.api.ObjectAssert;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public final class FunctionTestUtil {

    public static <T> void assertResult(final FEELFnResult<T> result, final T expectedResult, String...failMessage) {
        if (expectedResult == null) {
            assertResultNull(result, failMessage);
        } else {
            assertResultNotError(result, failMessage);
            final T resultValue = result.cata(left -> null, right -> right);
            assertThat(resultValue).isNotNull();
            assertThat(resultValue).isEqualTo(expectedResult);
        }
    }

    public static void assertResultBigDecimal(final FEELFnResult<BigDecimal> result, final BigDecimal expectedResult) {
        assertResultNotError(result);
        final BigDecimal resultValue = result.cata(left -> null, right -> right);
        assertThat(resultValue).isNotNull();
        assertThat(resultValue).isEqualTo(expectedResult);
    }

    public static <T> void assertResultList(final FEELFnResult<List<T>> result, final List<Object> expectedResult) {
        assertResultNotError(result);
        final List<T> resultList = result.cata(left -> null, right -> right);
        assertThat(resultList).hasSameSizeAs(expectedResult);
        if (expectedResult.isEmpty()) {
            assertThat(resultList).isEmpty();
        } else {
            assertThat(resultList).containsAll((Iterable<? extends T>) expectedResult);
        }
    }
    
    public static <T> void assertPredicateOnResult(final FEELFnResult<?> result, final Class<T> clazz, final Predicate<T> assertion) {
        assertResultNotError(result);
        final T resultValue = result.cata(left -> null, clazz::cast);
        assertThat(resultValue).isNotNull();
        assertThat(assertion.test(resultValue)).isTrue();
    }

    public static <T> void assertResultNull(final FEELFnResult<T> result, String...failMessage) {
        assertResultNotError(result, failMessage);
        T invokedResult = result.cata(left -> null, right -> right);
        assertThat(invokedResult).isNull();
    }

    public static <T> void assertResultNotError(final FEELFnResult<T> result, String...failMessage) {
        ObjectAssert<FEELFnResult<T>> feelFnResultObjectAssert = assertThat(result);
        AbstractBooleanAssert<?> abstractBooleanAssert = assertThat(result.isRight());
        if (failMessage.length == 1) {
            String message = checkingMessage(failMessage[0]);
            feelFnResultObjectAssert = feelFnResultObjectAssert.withFailMessage(message);
            abstractBooleanAssert = abstractBooleanAssert.withFailMessage(message);
        }
        feelFnResultObjectAssert.isNotNull();
        abstractBooleanAssert.isTrue();
    }

    public static <T> void assertResultError(final FEELFnResult<T> result, final Class expectedErrorEventClass, String...failMessage) {
        ObjectAssert<FEELFnResult<T>> feelFnResultObjectAssert = assertThat(result);
        AbstractBooleanAssert<?> abstractBooleanAssert = assertThat(result.isLeft());
        if (failMessage.length == 1) {
            String message = checkingMessage(failMessage[0]);
            feelFnResultObjectAssert = feelFnResultObjectAssert.withFailMessage(message);
            abstractBooleanAssert = abstractBooleanAssert.withFailMessage(message);
        }
        feelFnResultObjectAssert.isNotNull();
        abstractBooleanAssert.isTrue();
        final FEELEvent resultEvent = result.cata(left -> left, right -> null);
        checkErrorEvent(resultEvent, expectedErrorEventClass);
    }

    public static void checkErrorEvent(final FEELEvent errorEvent, final Class errorEventClass, String...failMessage) {
        ObjectAssert<FEELEvent> feelEventObjectAssert = assertThat(errorEvent);
        AbstractComparableAssert<?, FEELEvent.Severity> severityAbstractComparableAssert = assertThat(errorEvent.getSeverity());
        if (failMessage.length == 1) {
            String message = checkingMessage(failMessage[0]);
            feelEventObjectAssert = feelEventObjectAssert.withFailMessage(message);
            severityAbstractComparableAssert = severityAbstractComparableAssert.withFailMessage(message);
        }
        feelEventObjectAssert.isNotNull();
        feelEventObjectAssert.isInstanceOf(errorEventClass);
        severityAbstractComparableAssert.isEqualTo(FEELEvent.Severity.ERROR);
    }

    private static String checkingMessage(String failMessage) {
        return String.format("Checking `%s`", failMessage);
    }
    private FunctionTestUtil() {
        // Not allowed for util classes.
    }
}
