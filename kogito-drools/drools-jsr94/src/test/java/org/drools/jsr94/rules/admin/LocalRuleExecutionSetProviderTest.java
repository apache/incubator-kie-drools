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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

import org.drools.compiler.PackageBuilder;
import org.drools.jsr94.rules.RuleEngineTestBase;
import org.drools.rule.Package;

/**
 * Test the LocalRuleExecutionSetProvider implementation.
 *
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 * @author <a href="mailto:michael.frandsen@syngenio.de">michael frandsen </a>
 */
public class LocalRuleExecutionSetProviderTest extends RuleEngineTestBase {
    private RuleAdministrator             ruleAdministrator;

    private LocalRuleExecutionSetProvider ruleSetProvider;

    protected void setUp() throws Exception {
        super.setUp();
        this.ruleAdministrator = this.ruleServiceProvider.getRuleAdministrator();
        this.ruleSetProvider = this.ruleAdministrator.getLocalRuleExecutionSetProvider( null );
    }

    public void testCreateFromInputStream() throws Exception {
        final InputStream rulesStream = RuleEngineTestBase.class.getResourceAsStream( this.bindUri );
        final RuleExecutionSet ruleSet = this.ruleSetProvider.createRuleExecutionSet( rulesStream,
                                                                                      null );
        assertEquals( "rule set name",
                      "SistersRules",
                      ruleSet.getName() );
        assertEquals( "number of rules",
                      1,
                      ruleSet.getRules().size() );
    }

    public void testCreateFromObject() throws Exception {
        final InputStream inputStream = null;
        try {
            final PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( RuleEngineTestBase.class.getResourceAsStream( this.bindUri ) ) );
            final Package pkg = builder.getPackage();
            final RuleExecutionSet ruleExecutionSet = this.ruleSetProvider.createRuleExecutionSet( pkg,
                                                                                                   null );
            assertEquals( "rule set name",
                          "SistersRules",
                          ruleExecutionSet.getName() );
            assertEquals( "number of rules",
                          1,
                          ruleExecutionSet.getRules().size() );
        } catch ( final IOException e ) {
            fail( "Couldn't create the RuleExecutionSet. " + "Test threw an IOException." );
        } finally {
            if ( inputStream != null ) {
                try {
                    inputStream.close();
                } catch ( final IOException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Test createRuleExecutionSet from Reader.
     */
    public void testCreateFromReader() throws Exception {
        final Reader ruleReader = new InputStreamReader( RuleEngineTestBase.class.getResourceAsStream( this.bindUri ) );
        final RuleExecutionSet ruleSet = this.ruleSetProvider.createRuleExecutionSet( ruleReader,
                                                                                      null );
        assertEquals( "rule set name",
                      "SistersRules",
                      ruleSet.getName() );
        assertEquals( "number of rules",
                      1,
                      ruleSet.getRules().size() );
    }
}
