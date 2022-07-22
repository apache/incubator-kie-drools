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
package org.drools.verifier.core.index.keys;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueNullTest {

    @Test
    void testNull01() throws Exception {
        assertThat(new Value( null ).compareTo(new Value( null ))).isEqualTo(0);

    }

    @Test
    void testNull02() throws Exception {
        assertThat(new Value( -1 ).compareTo(new Value( 0 )) < 0).isTrue();
    }

    @Test
    void testNull03() throws Exception {
        assertThat(new Value( 0 ).compareTo(new Value( null )) > 0).isTrue();

    }
}