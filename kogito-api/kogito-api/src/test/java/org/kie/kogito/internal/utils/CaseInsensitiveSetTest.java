/*
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
package org.kie.kogito.internal.utils;

import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CaseInsensitiveSetTest {

    @Test
    void testCaseInsensitive() {
        final String upperCase = "Content-Length";
        final String lowerCase = upperCase.toLowerCase();
        Set<String> set = new CaseInsensitiveSet(upperCase);
        assertThat(set.contains(upperCase)).isTrue();
        assertThat(set.add(null)).isTrue();
        assertThat(set.add(lowerCase)).isFalse();
        assertThat(set.add(null)).isFalse();
        assertThat(set).hasSize(2);
        assertThat(set.contains(lowerCase)).isTrue();
        assertThat(set.contains(null)).isTrue();
        assertThat(set.remove(lowerCase)).isTrue();
        assertThat(set).hasSize(1);
        assertThat(set.remove(null)).isTrue();
        assertThat(set).isEmpty();
    }
}
