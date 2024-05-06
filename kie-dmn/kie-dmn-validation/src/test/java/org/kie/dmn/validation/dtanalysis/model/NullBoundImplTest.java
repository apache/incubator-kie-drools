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
package org.kie.dmn.validation.dtanalysis.model;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class NullBoundImplTest {

    public static final Logger LOG = LoggerFactory.getLogger(NullBoundImplTest.class);

    /**
     * assert the requirement over NullBoundImpl.NULL to always throw exception if attempting to use it.
     */
    @Test
    void test() {
        assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.compareTo(new Bound<>(BigDecimal.ONE, Range.RangeBoundary.CLOSED, null)));
        assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.getValue());
        assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.getBoundaryType());
        assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.getParent());
        assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.isLowerBound());
        assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.isUpperBound());
    }

    @Test
    void toStringInLogger() {
        LOG.info("{}", NullBoundImpl.NULL); // this could sometimes happen in debug mode
    }
}
