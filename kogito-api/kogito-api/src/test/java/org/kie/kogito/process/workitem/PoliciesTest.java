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
package org.kie.kogito.process.workitem;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.kie.kogito.auth.IdentityProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoliciesTest {

    @Test
    void testPolicies() {
        assertEquals(0, Policies.of(null).length);
        Policy<IdentityProvider>[] policies = Policies.of("pepe", Arrays.asList("chief", "of", "the", "universe"));
        assertEquals(1, policies.length);
        assertEquals("pepe", policies[0].value().getName());
        assertEquals(4, policies[0].value().getRoles().size());
    }
}
