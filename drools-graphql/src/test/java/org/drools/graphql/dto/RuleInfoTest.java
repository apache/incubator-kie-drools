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
package org.drools.graphql.dto;

import java.util.Map;

import org.kie.api.definition.rule.Rule;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuleInfoTest {

    @Test
    void shouldConvertFromRule() {
        Rule rule = mock(Rule.class);
        when(rule.getName()).thenReturn("Test Rule");
        when(rule.getPackageName()).thenReturn("com.example");
        when(rule.getLoadOrder()).thenReturn(5);
        when(rule.getMetaData()).thenReturn(Map.of("author", "alice", "version", "2"));

        RuleInfo info = RuleInfo.from(rule);

        assertThat(info.getName()).isEqualTo("Test Rule");
        assertThat(info.getPackageName()).isEqualTo("com.example");
        assertThat(info.getLoadOrder()).isEqualTo(5);
        assertThat(info.getMetadata()).hasSize(2);
        assertThat(info.getMetadata()).extracting(MetaEntry::getKey)
                .containsExactlyInAnyOrder("author", "version");
    }

    @Test
    void shouldHandleNullMetadata() {
        Rule rule = mock(Rule.class);
        when(rule.getName()).thenReturn("No Meta");
        when(rule.getPackageName()).thenReturn("pkg");
        when(rule.getLoadOrder()).thenReturn(0);
        when(rule.getMetaData()).thenReturn(null);

        RuleInfo info = RuleInfo.from(rule);
        assertThat(info.getMetadata()).isEmpty();
    }
}
