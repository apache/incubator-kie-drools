/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.checks.base;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CheckRunManagerRepeatingCommandTest {

    private HashSet<Check> checksToRun;

    @Before
    public void setUp() throws Exception {
        checksToRun = new HashSet<>();
        for (int i = 0; i < 100; i++) {

            checksToRun.add(mock(Check.class));
        }
    }

    @Test
    public void testRunAll() throws Exception {
        final ChecksRepeatingCommand checksRepeatingCommand = new ChecksRepeatingCommand(checksToRun,
                                                                                         null,
                                                                                         null);

        while (checksRepeatingCommand.execute()) {
            // Loopidiloop
        }

        final Check[] array = checksToRun.toArray(new Check[checksToRun.size()]);
        for (int i = 0; i < 100; i++) {
//            System.out.println( "i " + i );
            verify(array[i]).check();
        }
    }
}