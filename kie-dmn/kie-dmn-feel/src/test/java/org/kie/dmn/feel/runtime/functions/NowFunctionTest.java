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

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.functions.extended.NowFunction;

import static org.assertj.core.api.Assertions.assertThat;

class NowFunctionTest {

    private NowFunction nowFunction;

    @BeforeEach
    void setUp() {
        nowFunction = new NowFunction();
    }

    @Test
    void invoke() {
        // The current time that we need to compare will almost never be the same as another one we get for comparison purposes,
        // because there is some execution between them, so the comparison assertion doesn't make sense.
        // Note: We cannot guarantee any part of the date to be the same. E.g. in case when the test is executed
        // at the exact moment when the year is flipped to the next one, we cannot guarantee the year will be the same.

        final FEELFnResult<TemporalAccessor> nowResult = nowFunction.invoke();
        assertThat(nowResult.isRight()).isTrue();
        final TemporalAccessor result = nowResult.cata(left -> null, right -> right);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOfAny(ZonedDateTime.class);
    }

}