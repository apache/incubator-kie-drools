/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation.dtanalysis.model;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NullBoundImplTest {

    public static final Logger LOG = LoggerFactory.getLogger(NullBoundImplTest.class);

    /**
     * assert the requirement over NullBoundImpl.NULL to always throw exception if attempting to use it.
     */
    @Test
    public void test() {
        Assertions.assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.compareTo(new Bound<>(BigDecimal.ONE, Range.RangeBoundary.CLOSED, null)));
        Assertions.assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.getValue());
        Assertions.assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.getBoundaryType());
        Assertions.assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.getParent());
        Assertions.assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.isLowerBound());
        Assertions.assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.isUpperBound());
    }

    @Test
    public void testToStringInLogger() {
        LOG.info("{}", NullBoundImpl.NULL); // this could sometimes happen in debug mode
    }
}
