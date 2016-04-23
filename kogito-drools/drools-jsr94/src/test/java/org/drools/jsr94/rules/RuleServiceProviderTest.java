/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.jsr94.rules;

import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.admin.RuleAdministrator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the RuleServiceProvider implementation.
 */
public class RuleServiceProviderTest {
    /**
     * Test getRuleRuntime.
     */
    @Test
    public void testRuleRuntime() throws Exception {
        Class.forName("org.drools.jsr94.rules.RuleServiceProviderImpl");
        RuleServiceProvider ruleServiceProvider = RuleServiceProviderManager.getRuleServiceProvider("http://drools.org/");
        
        final RuleRuntime ruleRuntime = ruleServiceProvider.getRuleRuntime();
        assertNotNull( "cannot obtain RuleRuntime",
                       ruleRuntime );
        assertTrue( "not a class instance",
                    ruleRuntime == ruleServiceProvider.getRuleRuntime() );
    }

    /**
     * Test getRuleAdministrator.
     */
    @Test
    public void testRuleAdministrator() throws Exception {
        Class.forName("org.drools.jsr94.rules.RuleServiceProviderImpl");
        RuleServiceProvider ruleServiceProvider = RuleServiceProviderManager.getRuleServiceProvider("http://drools.org/");
        
        final RuleAdministrator ruleAdministrator = ruleServiceProvider.getRuleAdministrator();
        assertNotNull( "cannot obtain RuleAdministrator",
                       ruleAdministrator );
        assertTrue( "not a class instance",
                    ruleAdministrator == ruleServiceProvider.getRuleAdministrator() );
    }
}
