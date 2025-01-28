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
package org.kie.kogito.index;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonUtilsTest {

    @Test
    void testSimpleMergeMap() {
        Map<String, String> src = Map.of("name", "Javierito", "different", "remain");
        Map<String, String> target = Map.of("name", "Fulanito", "other", "remain");
        assertThat(CommonUtils.mergeMap(src, target)).isEqualTo(Map.of("name", "Javierito", "other", "remain", "different", "remain"));
    }

    @Test
    void testNullMergeMap() {
        Map<String, String> src = new HashMap<>();
        src.put("name", null);
        src.put("different", "remain");
        Map<String, String> target = Map.of("name", "Fulanito", "other", "remain");
        assertThat(CommonUtils.mergeMap(src, target)).isEqualTo(Map.of("name", "Fulanito", "other", "remain", "different", "remain"));
    }

    @Test
    void testComplexMergeMap() {
        Map<String, String> nestedSrc = Map.of("name", "Javierito", "different", "remain");
        Map<String, String> nestedTarget = Map.of("name", "Fulanito", "other", "remain");
        Map<String, Object> src = Map.of("nested", nestedSrc);
        Map<String, Object> target = Map.of("nested", nestedTarget);
        assertThat(CommonUtils.mergeMap(src, target)).isEqualTo(Map.of("nested", Map.of("name", "Javierito", "other", "remain", "different", "remain")));
    }

    @Test
    void testMergeWithEmptySource() {
        Map<String, String> target = Map.of("name", "Fulanito");
        assertThat(CommonUtils.mergeMap(Map.of(), target)).isEqualTo(target);
    }

    @Test
    void testMergeWithEmptyTarget() {
        Map<String, String> source = Map.of("name", "Javierito");
        assertThat(CommonUtils.mergeMap(source, Map.of())).isEqualTo(source);
    }

    @Test
    void testMergeWithNullSource() {
        Map<String, String> target = Map.of("name", "Gonzalito");
        assertThat(CommonUtils.mergeMap(null, target)).isEqualTo(target);
    }

    @Test
    void testMergeWithNullTarget() {
        Map<String, String> source = Map.of("name", "Francisquito");
        assertThat(CommonUtils.mergeMap(source, null)).isEqualTo(source);
    }

    @Test
    void testMergeWithNonMapNestedValueInSource() {
        Map<String, Object> src = Map.of("nested", "newValue");
        Map<String, Object> target = Map.of("nested", Map.of("key", "value"));
        assertThat(CommonUtils.mergeMap(src, target)).isEqualTo(Map.of("nested", "newValue"));
    }

    @Test
    void testMergeWithNonMapNestedValueInTarget() {
        Map<String, Object> src = Map.of("nested", Map.of("key", "newValue"));
        Map<String, Object> target = Map.of("nested", "oldValue");
        assertThat(CommonUtils.mergeMap(src, target)).isEqualTo(Map.of("nested", Map.of("key", "newValue")));
    }

    @Test
    void testMergeWithConflictingNonMapNestedValues() {
        Map<String, Object> src = Map.of("key", "newValue");
        Map<String, Object> target = Map.of("key", Map.of("nestedKey", "oldValue"));
        assertThat(CommonUtils.mergeMap(src, target)).isEqualTo(Map.of("key", "newValue"));
    }

    @Test
    void testMergeWithDeeplyNestedMaps() {
        Map<String, Object> src = Map.of("nested", Map.of("deepKey", Map.of("key1", "value1")));
        Map<String, Object> target = Map.of("nested", Map.of("deepKey", Map.of("key2", "value2")));
        assertThat(CommonUtils.mergeMap(src, target)).isEqualTo(
                Map.of("nested", Map.of("deepKey", Map.of("key1", "value1", "key2", "value2"))));
    }

    @Test
    void testMergeWithEmptyNestedMaps() {
        Map<String, Object> src = Map.of("nested", Map.of());
        Map<String, Object> target = Map.of("nested", Map.of("key", "value"));
        assertThat(CommonUtils.mergeMap(src, target)).isEqualTo(Map.of("nested", Map.of("key", "value")));
    }

    @Test
    void testMergeWithMixedKeyTypes() {
        Map<Object, String> src = Map.of(1, "value1", "key", "value2");
        Map<Object, String> target = Map.of(1, "value3", "otherKey", "value4");
        assertThat(CommonUtils.mergeMap(src, target)).isEqualTo(Map.of(1, "value1", "key", "value2", "otherKey", "value4"));
    }

    @Test
    void testMergeWithNullKey() {
        Map<Object, String> src = new HashMap<>();
        src.put(null, "value1");
        src.put("key", "value2");

        Map<Object, String> target = new HashMap<>();
        target.put(null, "value3");
        target.put("otherKey", "value4");

        Map<Object, String> expectedResult = new HashMap<>();
        expectedResult.put(null, "value1");
        expectedResult.put("key", "value2");
        expectedResult.put("otherKey", "value4");

        assertThat(CommonUtils.mergeMap(src, target)).isEqualTo(expectedResult);
    }

}
