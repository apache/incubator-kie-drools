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
package org.jbpm.compiler;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.util.DroolsStreamUtils;
import org.jbpm.process.core.Context;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.junit.Test;
import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;

public class PackageBuilderTest extends AbstractBaseTest {

    @Test
    public void testRuleFlow() throws Exception {
        InputStream in = this.getClass().getResourceAsStream( "/org/jbpm/integrationtests/ruleflow.rfm" );
        assertNotNull( in );

        builder.addPackage( new PackageDescr( "com.sample" ) );

        builder.addRuleFlow( new InputStreamReader( in ) );
        InternalKnowledgePackage pkg = builder.getPackage("com.sample");
        assertNotNull( pkg );

        Map<String, Process> flows = pkg.getRuleFlows();
        assertNotNull( flows );
        assertEquals( 1,
                      flows.size() );

        assertTrue( flows.containsKey( "0" ) );

        Process p = (Process) flows.get( "0" );
        assertTrue( p instanceof WorkflowProcessImpl );

        //now serialization
        InternalKnowledgePackage pkg2 = (InternalKnowledgePackage) DroolsStreamUtils.streamIn( DroolsStreamUtils.streamOut( pkg ) );
        assertNotNull( pkg2 );

        flows = pkg2.getRuleFlows();
        assertNotNull( flows );
        assertEquals( 1,
                      flows.size() );
        assertTrue( flows.containsKey( "0" ) );
        p = (Process) flows.get( "0" );
        assertTrue( p instanceof WorkflowProcessImpl );
    }

    @Test
    public void testRuleFlowUpgrade() throws Exception {
        // Set the system property so that automatic conversion can happen
        System.setProperty( "drools.ruleflow.port",
                            "true" );

        InputStream in = this.getClass().getResourceAsStream( "/org/jbpm/integrationtests/ruleflow40.rfm" );
        assertNotNull( in );

        builder.addPackage( new PackageDescr( "com.sample" ) );

        builder.addRuleFlow( new InputStreamReader( in ) );
        InternalKnowledgePackage pkg = builder.getPackage("com.sample");
        assertNotNull( pkg );

        Map<String, Process> flows = pkg.getRuleFlows();
        assertNotNull( flows );
        assertEquals( 1,
                      flows.size() );

        assertTrue( flows.containsKey( "0" ) );

        Process p = (Process) flows.get( "0" );
        assertTrue( p instanceof WorkflowProcessImpl );

        //now serialization
        InternalKnowledgePackage pkg2 = (InternalKnowledgePackage) DroolsStreamUtils.streamIn( DroolsStreamUtils.streamOut( pkg ) );
        assertNotNull( pkg2 );

        flows = pkg2.getRuleFlows();
        assertNotNull( flows );
        assertEquals( 1,
                      flows.size() );
        assertTrue( flows.containsKey( "0" ) );
        p = (Process) flows.get( "0" );
        assertTrue( p instanceof WorkflowProcessImpl );
        // Reset the system property so that automatic conversion should not happen
        System.setProperty( "drools.ruleflow.port",
                            "false" );
    }

    @Test
    public void testPackageRuleFlows() throws Exception {
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "boo" );
        Process rf = new MockRuleFlow( "1" );
        pkg.addProcess( rf );
        assertTrue( pkg.getRuleFlows().containsKey( "1" ) );
        assertSame( rf,
                    pkg.getRuleFlows().get( "1" ) );

        Process rf2 = new MockRuleFlow( "2" );
        pkg.addProcess( rf2 );
        assertTrue( pkg.getRuleFlows().containsKey( "1" ) );
        assertSame( rf,
                    pkg.getRuleFlows().get( "1" ) );
        assertTrue( pkg.getRuleFlows().containsKey( "2" ) );
        assertSame( rf2,
                    pkg.getRuleFlows().get( "2" ) );

        pkg.removeRuleFlow( "1" );
        assertTrue( pkg.getRuleFlows().containsKey( "2" ) );
        assertSame( rf2,
                    pkg.getRuleFlows().get( "2" ) );
        assertFalse( pkg.getRuleFlows().containsKey( "1" ) );

    }

    class MockRuleFlow
        implements
        Process {

        private String id;

        MockRuleFlow(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return null;
        }

        public String getType() {
            return null;
        }

        public String getVersion() {
            return null;
        }

        public String getPackageName() {
            return null;
        }

        public void setId(String id) {
        }

        public void setName(String name) {
        }

        public void setType(String type) {
        }

        public void setVersion(String version) {
        }

        public void setPackageName(String packageName) {
        }

        public void addContext(Context context) {
        }

        public List<Context> getContexts(String contextId) {
            return null;
        }

        public Context getDefaultContext(String contextId) {
            return null;
        }

        public void setDefaultContext(Context context) {
        }

        public Context getContext(String contextType,
                                  long id) {
            return null;
        }
        
    	public Map<String, Object> getMetaData() {
    		return null;
    	}

        public Object getMetaData(String name) {
            return null;
        }

        public void setMetaData(String name,
                                Object value) {
        }

        public Resource getResource() {
            return null;
        }

        public void setResource(Resource resource) {
        }

        public String[] getGlobalNames() {
            return null;
        }

        public Map<String, String> getGlobals() {
            return null;
        }

        public List<String> getImports() {
            return null;
        }

        public void setGlobals(Map<String, String> globals) {
        }

        public void setImports(List<String> imports) {
        }

        public List<String> getFunctionImports() {
            return null;
        }

        public void setFunctionImports(List<String> functionImports) {
        }

        public KnowledgeType getKnowledgeType() {
            return KnowledgeType.PROCESS;
        }

        public String getNamespace() {
            return null;
        }

    }

}
