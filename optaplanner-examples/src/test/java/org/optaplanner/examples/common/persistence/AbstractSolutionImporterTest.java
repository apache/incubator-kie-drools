/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class AbstractSolutionImporterTest {

    @Test
    public void factorial() {
        assertThat(AbstractSolutionImporter.factorial(1)).isEqualTo(BigInteger.valueOf(1));
        assertThat(AbstractSolutionImporter.factorial(2)).isEqualTo(BigInteger.valueOf(2));
        assertThat(AbstractSolutionImporter.factorial(3)).isEqualTo(BigInteger.valueOf(6));
        assertThat(AbstractSolutionImporter.factorial(4)).isEqualTo(BigInteger.valueOf(24));
    }
}
