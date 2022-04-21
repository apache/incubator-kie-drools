/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;

import static org.assertj.core.api.Assertions.assertThat;

public final class FunctionTestUtil {

    public static <T> void assertResult(final FEELFnResult<T> result, final T expectedResult) {
        if (expectedResult == null) {
            assertResultNull(result);
        } else {
            assertResultNotError(result);
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
        assertThat(resultList).hasSize(expectedResult.size());
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

    public static <T> void assertResultNull(final FEELFnResult<T> result) {
        assertResultNotError(result);
        T invokedResult = result.cata(left -> null, right -> right);
        assertThat(invokedResult).isNull();
    }

    public static <T> void assertResultNotError(final FEELFnResult<T> result) {
        assertThat(result).isNotNull();
        assertThat(result.isRight()).isTrue();
    }

    public static <T> void assertResultError(final FEELFnResult<T> result, final Class expectedErrorEventClass) {
        assertThat(result).isNotNull();
        assertThat(result.isLeft()).isTrue();
        final FEELEvent resultEvent = result.cata(left -> left, right -> null);
        checkErrorEvent(resultEvent, expectedErrorEventClass);
    }

    public static void checkErrorEvent(final FEELEvent errorEvent, final Class errorEventClass) {
        assertThat(errorEvent).isNotNull();
        assertThat(errorEvent.getSeverity()).isEqualTo(FEELEvent.Severity.ERROR);
        assertThat(errorEvent).isInstanceOf(errorEventClass);
    }

    private FunctionTestUtil() {
        // Not allowed for util classes.
    }
}
