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

package org.optaplanner.workbench.models.datamodel.util;

import java.util.Collection;
import java.util.function.Function;

import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.junit.Assert.*;

public class TemplateUtilsTest {

    @Test
    public void extractInterpolationVariablesContainsVariables() {
        String string = "foo bar @{var1} baz @{var2}";

        Collection<InterpolationVariable> interpolationVariables = TemplateUtils.extractInterpolationVariables(string);

        assertEquals(2,
                     interpolationVariables.size());
        assertTrue(interpolationVariables.contains(new InterpolationVariable("var1",
                                                                             DataType.TYPE_OBJECT)));
        assertTrue(interpolationVariables.contains(new InterpolationVariable("var2",
                                                                             DataType.TYPE_OBJECT)));
    }

    @Test
    public void extractInterpolationVariablesBrokenPattern() {
        String string = "foo @{zzz bar baz @";

        Collection<InterpolationVariable> interpolationVariables = TemplateUtils.extractInterpolationVariables(string);

        assertTrue(interpolationVariables.isEmpty());
    }

    @Test
    public void extractInterpolationVariablesNoVariables() {
        String string = "foo bar baz";

        Collection<InterpolationVariable> interpolationVariables = TemplateUtils.extractInterpolationVariables(string);

        assertTrue(interpolationVariables.isEmpty());
    }

    @Test
    public void substituteTemplateKey() {
        String string = "foo bar @{var1} baz @{var2}";

        Function<String, String> keyToValueFunction = s -> {
            switch (s) {
                case "var1":
                    return "val1";
                case "var2":
                    return "val2";
                default:
                    throw new IllegalArgumentException("Undefined variable " + s);
            }
        };

        String result = TemplateUtils.substituteTemplateVariable(string,
                                                                 keyToValueFunction);

        assertEquals("foo bar val1 baz val2",
                     result);
    }
}
