/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.impl;

import java.util.Collections;

import org.junit.Test;
import org.kie.dmn.core.BaseDMNContextTest;

public class DMNContextImplTest extends BaseDMNContextTest {

    @Test
    public void testEmptyContext() {
        DMNContextImpl ctx1 = new DMNContextImpl();
        testCloneAndAlter(ctx1, Collections.emptyMap(), Collections.emptyMap());

        DMNContextImpl ctx2 = new DMNContextImpl();
        testPushAndPopScope(ctx2, Collections.emptyMap(), Collections.emptyMap());
    }

    @Test
    public void testContextWithEntries() {
        DMNContextImpl ctx1 = new DMNContextImpl(DEFAULT_ENTRIES);
        testCloneAndAlter(ctx1, DEFAULT_ENTRIES, Collections.emptyMap());

        DMNContextImpl ctx2 = new DMNContextImpl(DEFAULT_ENTRIES);
        testPushAndPopScope(ctx2, DEFAULT_ENTRIES, Collections.emptyMap());
    }

    @Test
    public void testContextWithEntriesAndMetadata() {
        DMNContextImpl ctx1 = new DMNContextImpl(DEFAULT_ENTRIES, DEFAULT_METADATA);
        testCloneAndAlter(ctx1, DEFAULT_ENTRIES, DEFAULT_METADATA);

        DMNContextImpl ctx2 = new DMNContextImpl(DEFAULT_ENTRIES, DEFAULT_METADATA);
        testPushAndPopScope(ctx2, DEFAULT_ENTRIES, DEFAULT_METADATA);
    }

}
