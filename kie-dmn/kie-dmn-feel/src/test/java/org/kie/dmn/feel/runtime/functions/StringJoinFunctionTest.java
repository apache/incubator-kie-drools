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

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class StringJoinFunctionTest {

    private static final StringJoinFunction stringJoinFunction = StringJoinFunction.INSTANCE;

    @Test
    void setStringJoinFunctionNullValues() {
        FunctionTestUtil.assertResultError(stringJoinFunction.invoke( null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stringJoinFunction.invoke((List<?>) null , null), InvalidParametersEvent.class);
    }

    @Test
    void stringJoinFunctionEmptyList() {
        FunctionTestUtil.assertResult(stringJoinFunction.invoke(Collections.emptyList()), "");
        FunctionTestUtil.assertResult(stringJoinFunction.invoke(Collections.emptyList(), "X"), "");
    }

    @Test
    void stringJoinFunction() {
        FunctionTestUtil.assertResult(stringJoinFunction.invoke(Arrays.asList("a", "b", "c"), "_and_"), "a_and_b_and_c");
        FunctionTestUtil.assertResult(stringJoinFunction.invoke(Arrays.asList("a", "b", "c"), ""), "abc");
        FunctionTestUtil.assertResult(stringJoinFunction.invoke(Arrays.asList("a", "b", "c"), null), "abc");
        FunctionTestUtil.assertResult(stringJoinFunction.invoke(Arrays.asList("a"), "X"), "a");
        FunctionTestUtil.assertResult(stringJoinFunction.invoke(Arrays.asList("a", null, "c"), "X"), "aXc");
        FunctionTestUtil.assertResult(stringJoinFunction.invoke(Arrays.asList("a", "b", "c")), "abc");
        FunctionTestUtil.assertResult(stringJoinFunction.invoke(Arrays.asList("a", null, "c")), "ac");
    }

}
