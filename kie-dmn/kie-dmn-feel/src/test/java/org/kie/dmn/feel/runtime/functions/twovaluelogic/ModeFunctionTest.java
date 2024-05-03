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
package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

class ModeFunctionTest {

    private NNModeFunction modeFunction;

    @BeforeEach
    void setUp() {
        modeFunction = NNModeFunction.INSTANCE;
    }

    @Test
    void invokeListNull() {
        FunctionTestUtil.assertResult(modeFunction.invoke((List) null), null);
    }

    @Test
    void invokeListEmpty() {
        FunctionTestUtil.assertResult(modeFunction.invoke(Collections.emptyList()), null);
    }

    @Test
    void invokeListTypeHeterogenous() {
        FunctionTestUtil.assertResultError(modeFunction.invoke(Arrays.asList(1, "test")), InvalidParametersEvent.class);
    }

    @Test
    void invokeListParamSupportedTypesWithNull() {
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(20, 30, null, (long) 20, null, BigDecimal.TEN)),
                                      List.of(BigDecimal.valueOf(20)));
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(20, 30, null, (long) 20, 30, BigDecimal.TEN)),
                Arrays.asList( BigDecimal.valueOf(20), BigDecimal.valueOf(30) ));
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(null, null, null)),
                null);
    }

    @Test
    void invokeArrayNull() {
        FunctionTestUtil.assertResult(modeFunction.invoke((Object[]) null), null);
    }

    @Test
    void invokeArrayEmpty() {
        FunctionTestUtil.assertResult(modeFunction.invoke(new Object[]{}), null);
    }

    @Test
    void invokeArrayTypeHeterogenous() {
        FunctionTestUtil.assertResultError(modeFunction.invoke(new Object[]{1, "test"}), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayParamSupportedTypesWithNull() {
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(20, 30, null, (long) 20, null, BigDecimal.TEN)),
                                      List.of(BigDecimal.valueOf(20)));
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(20, 30, null, (long) 20, 30, BigDecimal.TEN)),
                Arrays.asList( BigDecimal.valueOf(20), BigDecimal.valueOf(30) ));
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(null, null, null)),
                null);
    }

}