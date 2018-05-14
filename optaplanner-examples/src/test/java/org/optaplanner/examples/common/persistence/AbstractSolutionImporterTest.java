/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.math.BigInteger;

import org.junit.Test;

import static org.junit.Assert.*;

public class AbstractSolutionImporterTest {

    @Test
    public void factorial() {
        assertEquals(BigInteger.valueOf(1), AbstractSolutionImporter.factorial(1));
        assertEquals(BigInteger.valueOf(2), AbstractSolutionImporter.factorial(2));
        assertEquals(BigInteger.valueOf(6), AbstractSolutionImporter.factorial(3));
        assertEquals(BigInteger.valueOf(24), AbstractSolutionImporter.factorial(4));
    }

}
