/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import static org.junit.Assert.*;

@Ignore( "This test causes problems to surefire (see same issue with org.drools.core.util.MVELSafeHelperTest) It works when executed by itself.")
public class WidMVELEvaluatorSafeTest extends WidMVELEvaluatorTest {

    private static TestSecurityManager tsm;

    @Before
    public void before() {
        try {
            String enginePolicy = getResouce("/policy/engine.policy");
            String kiePolicy = getResouce("/policy/kie.policy");
            System.setProperty("java.security.policy",
                               enginePolicy);
            System.setProperty("kie.security.policy",
                               kiePolicy);

            tsm = new TestSecurityManager();
            System.setSecurityManager(tsm);
        } catch (Exception e) {
            fail("unable to initiate security manager : " + e.getMessage());
        }
    }

    @After
    public void after() {
        System.setSecurityManager(null);
        System.setProperty("java.security.policy",
                           "");
        System.setProperty("kie.security.policy",
                           "");
    }

    public static class TestSecurityManager extends SecurityManager {

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ShouldHavePrevented("The security policy should have prevented the call to System.exit()");
        }
    }

    public static class ShouldHavePrevented extends SecurityException {

        public ShouldHavePrevented(String message) {
            super(message);
        }
    }
}
