/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.common.persistence;

import java.io.IOException;

import org.junit.Test;
import org.optaplanner.core.api.domain.solution.Solution;

import static org.junit.Assert.*;

public class AbstractTxtSolutionImporterTest {

    @Test
    public void splitBySpace() {
        AbstractTxtSolutionImporter.TxtInputBuilder inputBuilder = new AbstractTxtSolutionImporter.TxtInputBuilder() {
            @Override
            public Solution readSolution() throws IOException {
                return null;
            }
        };
        assertArrayEquals(new String[]{"one", "two", "three"},
                inputBuilder.splitBySpace("one two three"));
        assertArrayEquals(new String[]{"one", "two", "three"},
                inputBuilder.splitBySpace("one two \"three\"", null, null, false, true));
        assertArrayEquals(new String[]{"one", "two three"},
                inputBuilder.splitBySpace("one \"two three\"", null, null, false, true));
    }

}
