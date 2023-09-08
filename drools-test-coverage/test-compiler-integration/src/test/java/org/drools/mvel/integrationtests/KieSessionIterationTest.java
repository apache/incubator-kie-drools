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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests iteration through the list of KieSessions of a KieBase.
 */
public class KieSessionIterationTest {

    private KieBase kieBase;

    @Before
    public void setup() {
        this.kieBase = new KieHelper().build();
        // create several KieSessions
        this.kieBase.newKieSession();
        this.kieBase.newKieSession();
        this.kieBase.newKieSession();
    }

    @After
    public void cleanup() {
        if (this.kieBase != null) {
            // copying the KieSession collection is also workaround for ConcurrentModificationException in the test
            Collection<KieSession> kieSessions = new ArrayList<KieSession>();
            kieSessions.addAll(this.kieBase.getKieSessions());
            for (KieSession kieSession : kieSessions) {
                kieSession.dispose();
            }
        }
    }

    /**
     * Tests that disposing KieSessions does not throw ConcurrentModificationException when iterating through the list of KieBase's KieSessions
     * (related to BZ 1326329).
     */
    @Test
    public void testDisposingSeveralKieSessions() throws Exception {
        for (KieSession kieSession : this.kieBase.getKieSessions()) {
            kieSession.dispose();
        }
        assertThat(this.kieBase.getKieSessions().isEmpty()).as("All KieSessions of the KieBase should have been disposed.").isTrue();
    }

}
