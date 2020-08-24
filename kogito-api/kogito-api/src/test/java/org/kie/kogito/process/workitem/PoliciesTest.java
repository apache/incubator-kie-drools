package org.kie.kogito.process.workitem;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.kie.kogito.auth.IdentityProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoliciesTest {

    @Test
    void testPolicies() {
        assertEquals(0, Policies.of(null).length);
        Policy<IdentityProvider>[] policies = Policies.of("pepe", Arrays.asList("master", "of", "the", "universe"));
        assertEquals(1, policies.length);
        assertEquals("pepe", policies[0].value().getName());
        assertEquals(4, policies[0].value().getRoles().size());
    }
}
