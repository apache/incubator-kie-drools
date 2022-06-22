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

package org.kie.dmn.feel.gwt.functions.api;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.lang.types.BuiltInType.STRING;

public class FunctionOverrideVariationTest {

    private FunctionOverrideVariation functionOverrideVariation;

    @Before
    public void setup() {

        final Parameter input = new Parameter("input", STRING);
        final Parameter pattern = new Parameter("pattern", STRING);
        final Parameter replacement = new Parameter("replacement", STRING);
        final Parameter flags = new Parameter("flags", STRING);

        functionOverrideVariation = new FunctionOverrideVariation(STRING, "replace", input, pattern, replacement, flags);
    }

    @Test
    public void testToHumanReadableString() {
        assertThat(functionOverrideVariation.toHumanReadableString()).isEqualTo("string, string, string, string");
    }

    @Test
    public void testToHumanReadableStrings() {
        final FunctionDefinitionStrings functionDefinitionStrings = functionOverrideVariation.toHumanReadableStrings();
        assertThat(functionDefinitionStrings.getHumanReadable()).isEqualTo("replace(string, string, string, string)");
        assertThat(functionDefinitionStrings.getTemplate()).isEqualTo("replace($1, $2, $3, $4)");
    }
}
