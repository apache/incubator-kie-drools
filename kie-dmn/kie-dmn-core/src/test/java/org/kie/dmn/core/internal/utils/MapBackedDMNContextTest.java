/**
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
package org.kie.dmn.core.internal.utils;

import java.util.Collections;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.kie.dmn.core.BaseDMNContextTest;

class MapBackedDMNContextTest extends BaseDMNContextTest {

    @Test
    void emptyContext() {
        MapBackedDMNContext ctx1 = MapBackedDMNContext.of(new HashMap<>(Collections.emptyMap()));
        testCloneAndAlter(ctx1, Collections.emptyMap(), Collections.emptyMap());

        MapBackedDMNContext ctx2 = MapBackedDMNContext.of(new HashMap<>(Collections.emptyMap()));
        testPushAndPopScope(ctx2, Collections.emptyMap(), Collections.emptyMap());
    }

    @Test
    void contextWithEntries() {
        MapBackedDMNContext ctx1 = MapBackedDMNContext.of(new HashMap<>(DEFAULT_ENTRIES));
        testCloneAndAlter(ctx1, DEFAULT_ENTRIES, Collections.emptyMap());

        MapBackedDMNContext ctx2 = MapBackedDMNContext.of(new HashMap<>(DEFAULT_ENTRIES));
        testPushAndPopScope(ctx2, DEFAULT_ENTRIES, Collections.emptyMap());
    }

    @Test
    void contextWithEntriesAndMetadata() {
        MapBackedDMNContext ctx1 = MapBackedDMNContext.of(new HashMap<>(DEFAULT_ENTRIES), new HashMap<>(DEFAULT_METADATA));
        testCloneAndAlter(ctx1, DEFAULT_ENTRIES, DEFAULT_METADATA);

        MapBackedDMNContext ctx2 = MapBackedDMNContext.of(new HashMap<>(DEFAULT_ENTRIES), new HashMap<>(DEFAULT_METADATA));
        testPushAndPopScope(ctx2, DEFAULT_ENTRIES, DEFAULT_METADATA);
    }

}
