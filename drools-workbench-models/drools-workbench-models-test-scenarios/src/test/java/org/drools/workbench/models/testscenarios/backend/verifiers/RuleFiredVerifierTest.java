/*
* Copyright 2010 JBoss Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.drools.workbench.models.testscenarios.backend.verifiers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.drools.workbench.models.testscenarios.shared.VerifyRuleFired;

public class RuleFiredVerifierTest {

    @Test
    public void testCountVerification() throws Exception {

        Map<String, Integer> firingCounts = new HashMap<String, Integer>();
        firingCounts.put("foo",
                2);
        firingCounts.put("bar",
                1);
        // and baz, we leave out

        RuleFiredVerifier ruleFiredVerifier = new RuleFiredVerifier();
        ruleFiredVerifier.setFireCounter(firingCounts);

        VerifyRuleFired v = new VerifyRuleFired();
        v.setRuleName("foo");
        v.setExpectedFire(true);

        ruleFiredVerifier.verifyFiringCounts(v);
        assertTrue(v.getSuccessResult());
        assertEquals(2,
                v.getActualResult().intValue());

        v = new VerifyRuleFired();
        v.setRuleName("foo");
        v.setExpectedFire(false);
        ruleFiredVerifier.verifyFiringCounts(v);
        assertFalse(v.getSuccessResult());
        assertEquals(2,
                v.getActualResult().intValue());
        assertNotNull(v.getExplanation());

        v = new VerifyRuleFired();
        v.setRuleName("foo");
        v.setExpectedCount(2);

        ruleFiredVerifier.verifyFiringCounts(v);
        assertTrue(v.getSuccessResult());
        assertEquals(2,
                v.getActualResult().intValue());

    }


    @Test
    public void testRuleFiredWithEnum() throws Exception {
        Map<String, Integer> firingCounts = new HashMap<String, Integer>();
        firingCounts.put("foo",
                2);
        firingCounts.put("bar",
                1);
        // and baz, we leave out

        RuleFiredVerifier ruleFiredVerifier = new RuleFiredVerifier();
        ruleFiredVerifier.setFireCounter(firingCounts);
        VerifyRuleFired v = new VerifyRuleFired();
        v.setRuleName("foo");
        v.setExpectedFire(true);
        ruleFiredVerifier.verifyFiringCounts(v);
        assertTrue(v.getSuccessResult());
        assertEquals(2,
                v.getActualResult().intValue());
    }

    @Test
    public void testVerifyRuleFired() throws Exception {

        RuleFiredVerifier ruleFiredVerifier = new RuleFiredVerifier();

        VerifyRuleFired vr = new VerifyRuleFired("qqq",
                42,
                null);
        Map<String, Integer> f = new HashMap<String, Integer>();
        f.put("qqq",
                42);
        f.put("qaz",
                1);

        ruleFiredVerifier.setFireCounter(f);
        ruleFiredVerifier.verifyFiringCounts(vr);
        assertTrue(vr.wasSuccessful());
        assertEquals(42,
                vr.getActualResult().intValue());

        vr = new VerifyRuleFired("qqq",
                41,
                null);

        ruleFiredVerifier.setFireCounter(f);
        ruleFiredVerifier.verifyFiringCounts(vr);
        assertFalse(vr.wasSuccessful());
        assertEquals(42,
                vr.getActualResult().intValue());

        vr = new VerifyRuleFired("qaz",
                1,
                null);

        ruleFiredVerifier.setFireCounter(f);
        ruleFiredVerifier.verifyFiringCounts(vr);
        assertTrue(vr.wasSuccessful());
        assertEquals(1,
                vr.getActualResult().intValue());

        vr = new VerifyRuleFired("XXX",
                null,
                false);

        ruleFiredVerifier.setFireCounter(f);
        ruleFiredVerifier.verifyFiringCounts(vr);
        assertTrue(vr.wasSuccessful());
        assertEquals(0,
                vr.getActualResult().intValue());

        vr = new VerifyRuleFired("qqq",
                null,
                true);

        ruleFiredVerifier.setFireCounter(f);
        ruleFiredVerifier.verifyFiringCounts(vr);
        assertTrue(vr.wasSuccessful());
        assertEquals(42,
                vr.getActualResult().intValue());

        vr = new VerifyRuleFired("qqq",
                null,
                false);

        ruleFiredVerifier.setFireCounter(f);
        ruleFiredVerifier.verifyFiringCounts(vr);
        assertFalse(vr.wasSuccessful());
        assertEquals(42,
                vr.getActualResult().intValue());

    }
}
