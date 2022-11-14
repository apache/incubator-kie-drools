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

import static org.assertj.core.api.Assertions.assertThat;

class PoliciesTest {

    @Test
    void testPolicies() {
        assertThat(Policies.of(null)).isEmpty();
        Policy<IdentityProvider>[] policies = Policies.of("pepe", Arrays.asList("chief", "of", "the", "universe"));
        assertThat(policies).hasSize(1);
        assertThat(policies[0].value().getName()).isEqualTo("pepe");
        assertThat(policies[0].value().getRoles()).hasSize(4);
    }
}
