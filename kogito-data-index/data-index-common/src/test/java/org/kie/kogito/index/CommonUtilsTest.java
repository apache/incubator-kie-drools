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
}
