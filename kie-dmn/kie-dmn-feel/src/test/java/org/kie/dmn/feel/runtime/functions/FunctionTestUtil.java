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
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;

public final class FunctionTestUtil {

    public static <T> void assertResult(final FEELFnResult<T> result, final T expectedResult) {
        if (expectedResult == null) {
            assertResultNull(result);
        } else {
            assertResultNotError(result);
            final T resultValue = result.cata(left -> null, right -> right);
            Assert.assertThat(resultValue, Matchers.notNullValue());
            Assert.assertThat(resultValue, Matchers.equalTo(expectedResult));
        }
    }

    public static void assertResultBigDecimal(final FEELFnResult<BigDecimal> result, final BigDecimal expectedResult) {
        assertResultNotError(result);
        final BigDecimal resultValue = result.cata(left -> null, right -> right);
        Assert.assertThat(resultValue, Matchers.notNullValue());
        Assert.assertThat(resultValue, Matchers.comparesEqualTo(expectedResult));
    }

    public static <T> void assertResultList(final FEELFnResult<List<T>> result, final List<Object> expectedResult) {
        assertResultNotError(result);
        final List<T> resultList = result.cata(left -> null, right -> right);
        Assert.assertThat(resultList, Matchers.hasSize(expectedResult.size()));
        if (expectedResult.isEmpty()) {
            Assert.assertThat(resultList, Matchers.empty());
        } else {
            Assert.assertThat(resultList, Matchers.contains(expectedResult.toArray(new Object[]{})));
        }
    }

    public static <T> void assertResultNull(final FEELFnResult<T> result) {
        assertResultNotError(result);
        Assert.assertThat(result.cata(left -> false, right -> right), Matchers.nullValue());
    }

    public static <T> void assertResultNotError(final FEELFnResult<T> result) {
        Assert.assertThat(result, Matchers.notNullValue());
        Assert.assertThat(result.isRight(), Matchers.is(true));
    }

    public static <T> void assertResultError(final FEELFnResult<T> result, final Class expectedErrorEventClass) {
        Assert.assertThat(result, Matchers.notNullValue());
        Assert.assertThat(result.isLeft(), Matchers.is(true));
        final FEELEvent resultEvent = result.cata(left -> left, right -> null);
        checkErrorEvent(resultEvent, expectedErrorEventClass);
    }

    public static void checkErrorEvent(final FEELEvent errorEvent, final Class errorEventClass) {
        Assert.assertThat(errorEvent, Matchers.notNullValue());
        Assert.assertThat(errorEvent.getSeverity(), Matchers.is(FEELEvent.Severity.ERROR));
        Assert.assertThat(errorEvent, Matchers.instanceOf(errorEventClass));
    }

    private FunctionTestUtil() {
        // Not allowed for util classes.
    }
}
