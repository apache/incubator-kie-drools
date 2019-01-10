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

package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModeFunctionTest {

    private NNModeFunction modeFunction;

    @Before
    public void setUp() {
        modeFunction = NNModeFunction.INSTANCE;
    }

    @Test
    public void invokeListNull() {
        FunctionTestUtil.assertResult(modeFunction.invoke((List) null), null);
    }

    @Test
    public void invokeListEmpty() {
        FunctionTestUtil.assertResult(modeFunction.invoke(Collections.emptyList()), null);
    }

    @Test
    public void invokeListTypeHeterogenous() {
        FunctionTestUtil.assertResultError(modeFunction.invoke(Arrays.asList(1, "test")), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListParamSupportedTypesWithNull() {
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(20, 30, null, (long) 20, null, BigDecimal.TEN)),
                Arrays.asList( BigDecimal.valueOf(20) ));
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(20, 30, null, (long) 20, 30, BigDecimal.TEN)),
                Arrays.asList( BigDecimal.valueOf(20), BigDecimal.valueOf(30) ));
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(null, null, null)),
                null);
    }

    @Test
    public void invokeArrayNull() {
        FunctionTestUtil.assertResult(modeFunction.invoke((Object[]) null), null);
    }

    @Test
    public void invokeArrayEmpty() {
        FunctionTestUtil.assertResult(modeFunction.invoke(new Object[]{}), null);
    }

    @Test
    public void invokeArrayTypeHeterogenous() {
        FunctionTestUtil.assertResultError(modeFunction.invoke(new Object[]{1, "test"}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeArrayParamSupportedTypesWithNull() {
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(20, 30, null, (long) 20, null, BigDecimal.TEN)),
                Arrays.asList( BigDecimal.valueOf(20) ));
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(20, 30, null, (long) 20, 30, BigDecimal.TEN)),
                Arrays.asList( BigDecimal.valueOf(20), BigDecimal.valueOf(30) ));
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(null, null, null)),
                null);
    }

}