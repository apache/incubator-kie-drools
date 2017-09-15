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

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NowFunctionTest {

    private NowFunction nowFunction;

    @Before
    public void setUp() {
        nowFunction = new NowFunction();
    }

    @Test
    public void invoke() {
        // The current time that we need to compare will almost never be the same as another one we get for comparison purposes,
        // because there is some execution between them, so the comparison assertion doesn't make sense.
        // Note: We cannot guarantee any part of the date to be the same. E.g. in case when the test is executed
        // at the exact moment when the year is flipped to the next one, we cannot guarantee the year will be the same.

        FEELFnResult<TemporalAccessor> nowResult = nowFunction.invoke();
        Assert.assertThat(nowResult.isRight(), Matchers.is(true));
        final TemporalAccessor result = nowResult.cata(left -> null, right -> right);
        Assert.assertThat(result, Matchers.notNullValue());
        Assert.assertThat(result.getClass(), Matchers.typeCompatibleWith(ZonedDateTime.class));
    }

}