/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.documentation;

import java.util.Collection;

import org.junit.runners.Parameterized;
import org.kie.dmn.feel.runtime.BaseFEELTest;

/**
 * Some examples (/tests) from the DMN spec were omitted in the ADOC due to policy about specific keywords
 * Those tests are placed here to make sure all the examples from the DMN spec are integrated and running as expected.
 */
public class FromSpecificationNotInAdocTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{3}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                {"substring(\"foobar\",3) = \"obar\"", Boolean.TRUE, null},
                {"substring(\"foobar\",3,3) = \"oba\"", Boolean.TRUE, null},
                {"substring(\"foobar\", -2, 1) = \"a\"", Boolean.TRUE, null},
                {"string length(\"foo\") = 3", Boolean.TRUE, null},
                {"substring before(\"foobar\", \"bar\") = \"foo\"", Boolean.TRUE, null},
                {"substring before(\"foobar\", \"xyz\") = \"\"", Boolean.TRUE, null},
                {"substring after(\"foobar\", \"ob\") = \"ar\"", Boolean.TRUE, null},
                {"contains(\"foobar\", \"of\") = false", Boolean.TRUE, null},
                {"starts with(\"foobar\", \"fo\") = true", Boolean.TRUE, null},
                {"ends with(\"foobar\", \"r\") = true", Boolean.TRUE, null},
                {"matches(\"foobar\", \"^fo*b\") = true", Boolean.TRUE, null},
        };
        return addAdditionalParameters(cases, false);
    }
}
