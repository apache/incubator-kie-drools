/**
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

package org.drools.jsr94.rules.admin;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.InputStream;

import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

import org.drools.jsr94.rules.RuleEngineTestBase;

/**
 * Test the RuleExecutionSet implementation.
 *
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 * @author <a href="mailto:michael.frandsen@syngenio.de">michael frandsen </a>
 */
public class RuleExecutionSetTest extends RuleEngineTestBase {

    private RuleAdministrator             ruleAdministrator;

    private LocalRuleExecutionSetProvider ruleSetProvider;

    /**
     * Setup the test case.
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.ruleAdministrator = this.ruleServiceProvider.getRuleAdministrator();
        this.ruleSetProvider = this.ruleAdministrator.getLocalRuleExecutionSetProvider( null );
    }

    /**
     * Test rule set name and description.
     */
    public void testRule() throws Exception {
        final InputStream in = RuleEngineTestBase.class.getResourceAsStream( this.bindUri );
        final RuleExecutionSet ruleSet = this.ruleSetProvider.createRuleExecutionSet( in,
                                                                                      null );
        assertEquals( "number of rules",
                      1,
                      ruleSet.getRules().size() );

        assertEquals( "rule set name",
                      "SistersRules",
                      ruleSet.getName() );
        assertEquals( "SistersRules",
                      ruleSet.getDescription() );
        assertNull( "rule set default filter",
                    ruleSet.getDefaultObjectFilter() );
    }
}
